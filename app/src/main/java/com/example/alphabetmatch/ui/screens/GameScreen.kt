package com.example.alphabetmatch.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabetmatch.data.RoundGenerator
import com.example.alphabetmatch.ui.AlphabetViewModel
import com.example.alphabetmatch.ui.Feedback
import com.example.alphabetmatch.ui.components.BackButton
import com.example.alphabetmatch.ui.components.ConfettiOverlay
import com.example.alphabetmatch.ui.components.PictureCard
import com.example.alphabetmatch.ui.components.shake
import com.example.alphabetmatch.ui.theme.CoralRed
import com.example.alphabetmatch.ui.theme.FreshGreen
import com.example.alphabetmatch.ui.theme.InkNavy
import com.example.alphabetmatch.ui.theme.PastelSky
import com.example.alphabetmatch.ui.theme.SkyBlue
import com.example.alphabetmatch.ui.theme.SunnyYellow

/** Identifies a tappable node: left = a letter, right = a picture. */
private data class NodeId(val isLeft: Boolean, val index: Int)

/**
 * GameScreen: 3 letters in a left column, 3 pictures in a right column.
 * The child connects a letter to the matching picture by DRAGGING a line or by
 * TAP-selecting one side then the other. Green line = correct connection.
 *
 * Gestures are handled by one detector on the play-area container so taps and
 * drags are seen consistently; the nodes themselves are purely visual.
 */
@Composable
fun GameScreen(
    state: AlphabetViewModel.UiState,
    onConnect: (Int, Int) -> Unit,
    onBackRequested: () -> Unit,
    onConfirmExit: () -> Unit,
    onDismissExit: () -> Unit,
) {
    val round = state.round

    // Coordinate bookkeeping: container origin (root space) + each node's bounds (root space).
    var containerOrigin by remember { mutableStateOf(Offset.Zero) }
    val nodeRects = remember { mutableStateMapOf<NodeId, Rect>() }

    // Transient interaction state (reset whenever a new round starts).
    var selected by remember(round) { mutableStateOf<NodeId?>(null) }
    var dragFrom by remember(round) { mutableStateOf<NodeId?>(null) }
    var dragPos by remember(round) { mutableStateOf(Offset.Zero) }
    var dragActive by remember(round) { mutableStateOf(false) }

    // root = container-local point + container's origin.
    fun hitTest(local: Offset): NodeId? {
        val root = local + containerOrigin
        for (i in 0 until RoundGenerator.PAIRS) {
            if (nodeRects[NodeId(true, i)]?.contains(root) == true) return NodeId(true, i)
        }
        for (i in 0 until RoundGenerator.PAIRS) {
            if (nodeRects[NodeId(false, i)]?.contains(root) == true) return NodeId(false, i)
        }
        return null
    }

    fun resolve(a: NodeId, b: NodeId) {
        if (a.isLeft == b.isLeft) return
        val left = if (a.isLeft) a.index else b.index
        val right = if (a.isLeft) b.index else a.index
        onConnect(left, right)
    }

    fun handleTap(node: NodeId) {
        val sel = selected
        if (sel == null || sel.isLeft == node.isLeft) {
            selected = node
        } else {
            resolve(sel, node)
            selected = null
        }
    }

    fun setDrag(from: NodeId?, pos: Offset, active: Boolean) {
        dragFrom = from
        dragPos = pos
        dragActive = active
    }

    Box(modifier = Modifier.fillMaxSize().background(PastelSky)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
        ) {
            // ---- Top bar: back / level header / stars ----
            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButton(onClick = onBackRequested, contentDescription = "Back to home")
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Level ${state.level}",
                        color = InkNavy,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Text(
                    text = "\u2605 ${state.stars}",
                    color = SunnyYellow,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ---- Play area: letters (left) ↔ pictures (right) with connecting lines ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned { containerOrigin = it.positionInRoot() }
                    .pointerInput(round) {
                        matchGesture(
                            findNode = { hitTest(it) },
                            onTapNode = { handleTap(it) },
                            onConnectPair = { a, b -> resolve(a, b) },
                            onDrag = { f, p, a -> setDrag(f, p, a) },
                        )
                    },
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Left column: 3 letters.
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        round.leftLetters.forEachIndexed { i, letter ->
                            val matched = state.matched.any { it.first == i }
                            val isSel = selected == NodeId(true, i)
                            val border = when {
                                matched -> FreshGreen
                                isSel -> SkyBlue
                                else -> Color.Transparent
                            }
                            MatchBox(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                containerColor = SunnyYellow,
                                borderColor = border,
                                onBounds = { nodeRects[NodeId(true, i)] = it },
                            ) {
                                Text(
                                    text = letter.toString(),
                                    color = InkNavy,
                                    fontSize = 72.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                )
                            }
                        }
                    }

                    // Gap for the connecting lines to cross.
                    Spacer(modifier = Modifier.weight(1f))

                    // Right column: 3 pictures.
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        round.rightWords.forEachIndexed { i, word ->
                            val matched = state.matched.any { it.second == i }
                            val isSel = selected == NodeId(false, i)
                            val border = when {
                                matched -> FreshGreen
                                isSel -> SkyBlue
                                else -> Color.Transparent
                            }
                            val shaking =
                                state.wrongRightIndex == i && state.feedback == Feedback.Wrong
                            MatchBox(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                containerColor = Color.White,
                                borderColor = border,
                                shakeKey = if (shaking) state.shakeKey else 0,
                                onBounds = { nodeRects[NodeId(false, i)] = it },
                            ) {
                                PictureCard(
                                    imageRes = word.imageRes,
                                    label = word.label,
                                    modifier = Modifier.fillMaxSize(),
                                    borderColor = border,
                                )
                            }
                        }
                    }
                }

                // Overlay that draws the connecting lines (matched + live drag).
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val origin = containerOrigin

                    // Edge anchors: a left box emits from its RIGHT edge, a right box
                    // receives on its LEFT edge - both at the vertical middle of that edge.
                    fun rightEdge(rect: Rect): Offset = Offset(rect.right, rect.center.y) - origin
                    fun leftEdge(rect: Rect): Offset = Offset(rect.left, rect.center.y) - origin

                    state.matched.forEach { (l, r) ->
                        val lr = nodeRects[NodeId(true, l)]
                        val rr = nodeRects[NodeId(false, r)]
                        if (lr != null && rr != null) {
                            val start = rightEdge(lr)
                            val end = leftEdge(rr)
                            drawLine(
                                color = FreshGreen,
                                start = start,
                                end = end,
                                strokeWidth = 12f,
                                cap = StrokeCap.Round,
                            )
                            // Dots at each connection point for a cleaner look.
                            drawCircle(color = FreshGreen, radius = 10f, center = start)
                            drawCircle(color = FreshGreen, radius = 10f, center = end)
                        }
                    }
                    if (dragActive) {
                        val from = dragFrom
                        val src = from?.let { nodeRects[it] }
                        if (src != null && from != null) {
                            // Line emanates from the source box's edge facing the gap.
                            val start = if (from.isLeft) rightEdge(src) else leftEdge(src)
                            drawLine(
                                color = SkyBlue,
                                start = start,
                                end = dragPos,
                                strokeWidth = 12f,
                                cap = StrokeCap.Round,
                            )
                            drawCircle(color = SkyBlue, radius = 10f, center = start)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Strikes: 3 wrong connections in a level restarts at Level 1.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(3) { i ->
                    val lost = i < state.mistakes
                    Text(
                        text = if (lost) "\u2717" else "\u2665",
                        color = if (lost) CoralRed else FreshGreen,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(horizontal = 6.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Feedback line (notice overrides transient correct/wrong text).
            Box(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                val notice = state.notice
                when {
                    notice != null -> Text(
                        text = notice,
                        color = CoralRed,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    state.feedback == Feedback.Wrong -> Text(
                        text = "Try again!",
                        color = CoralRed,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    state.feedback == Feedback.Correct -> Text(
                        text = "Great job!",
                        color = FreshGreen,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    else -> Text(
                        text = "Match each letter to its picture!",
                        color = InkNavy,
                        fontSize = 22.sp,
                    )
                }
            }
        }

        if (state.feedback == Feedback.Correct) {
            ConfettiOverlay(active = true, modifier = Modifier.fillMaxSize())
        }
        if (state.showExitDialog) {
            ExitGameDialog(onConfirmExit = onConfirmExit, onDismissExit = onDismissExit)
        }
    }
}

/**
 * One game node: a SQUARE card centred in its weighted slot (same width and height,
 * so 512x512 art renders undistorted), plus bounds recording for hit testing and an
 * optional shake. The caller's [content] draws the visible card/letter.
 */
@Composable
private fun MatchBox(
    modifier: Modifier = Modifier,
    containerColor: Color,
    borderColor: Color,
    shakeKey: Int = 0,
    onBounds: (Rect) -> Unit,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier.padding(horizontal = 6.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Square: the smaller of the slot's two sides, centred in the slot.
        val side = minOf(maxWidth, maxHeight)
        Box(
            modifier = Modifier
                .size(side)
                .onGloballyPositioned { onBounds(it.boundsInRoot()) }
                .clip(RoundedCornerShape(24.dp))
                .background(containerColor)
                .border(4.dp, borderColor, RoundedCornerShape(24.dp))
                .shake(shakeKey),
            contentAlignment = Alignment.Center,
            content = { content() },
        )
    }
}

/**
 * Single gesture handler for the play area. Distinguishes TAP (select / connect) from
 * DRAG (draw a line) using touch slop; positions are in container-local coordinates.
 * A drag dropped on nothing cancels silently (toddlers wiggle) – no penalty.
 */
private suspend fun PointerInputScope.matchGesture(
    findNode: (Offset) -> NodeId?,
    onTapNode: (NodeId) -> Unit,
    onConnectPair: (NodeId, NodeId) -> Unit,
    onDrag: (NodeId?, Offset, Boolean) -> Unit,
) {
    val slop = viewConfiguration.touchSlop
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val start = findNode(down.position)
        if (start == null) {
            // Touch began on empty space: swallow it until released.
            while (true) {
                val ev = awaitPointerEvent()
                if (ev.changes.none { it.pressed }) break
            }
            return@awaitEachGesture
        }
        var dragging = false
        var current = down.position
        while (true) {
            val ev = awaitPointerEvent()
            val ch = ev.changes.firstOrNull { it.id == down.id } ?: break
            if (!ch.pressed) {
                current = ch.position
                if (!dragging) onTapNode(start)
                break
            }
            if (!dragging && (ch.position - down.position).getDistance() > slop) {
                dragging = true
                onDrag(start, ch.position, true)
            }
            if (dragging) {
                current = ch.position
                onDrag(start, current, true)
            }
            if (ev.changes.none { it.pressed }) break
        }
        if (dragging) {
            onDrag(null, Offset.Zero, false)
            val target = findNode(current)
            if (target != null) onConnectPair(start, target)
        }
    }
}

