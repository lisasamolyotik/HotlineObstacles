package com.obstacles.hotline.ui.fragments

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.obstacles.hotline.R
import com.obstacles.hotline.databinding.LevelScreenBinding
import com.obstacles.hotline.model.Line
import com.obstacles.hotline.util.Constants
import com.obstacles.hotline.util.DimensionConverter
import com.obstacles.hotline.util.Direction
import kotlin.math.absoluteValue
import kotlin.random.Random

class LevelFragment : Fragment(R.layout.level_screen), GestureDetector.OnGestureListener {
    private var ballAnimator: ObjectAnimator? = null
    private var layoutChangedListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var ballDistance = 0f
    private lateinit var mDetector: GestureDetectorCompat
    private val topRectangleLocation = IntArray(2)
    private var xDelta = 0f
    private var level = 1
    private val lines = mutableListOf<Line>()
    private val startWidth = 457
    private var callback: OnBackPressedCallback? = null

    private val args: LevelFragmentArgs by navArgs()
    private var _binding: LevelScreenBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LevelScreenBinding.inflate(inflater, container, false)

        mDetector = GestureDetectorCompat(requireContext(), this)
        binding.container.setOnTouchListener { _, event ->
            mDetector.onTouchEvent(event).let {
                true
            }
        }

        level = args.level
        Log.d("tag", "level $level")
        addLines()
        layoutChangedListener = ViewTreeObserver.OnGlobalLayoutListener {
            ballDistance = (binding.bottomBorder.top - binding.topRectangle.bottom).toFloat()
            binding.topRectangle.getLocationInWindow(topRectangleLocation)
            for (line in lines) {
                moveLine(line)
            }
            binding.container.viewTreeObserver.removeOnGlobalLayoutListener(layoutChangedListener)
            layoutChangedListener = null
        }
        binding.container.viewTreeObserver.addOnGlobalLayoutListener(layoutChangedListener)

        moveBottomRectangle()

        callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            binding.root.findNavController().navigate(R.id.action_levelFragment_to_menuFragment)
            isEnabled = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.optionsButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_levelFragment_to_gameSettingsFragment)
        }
    }

    private fun addLines() {
        binding.line.visibility = View.INVISIBLE
        val params =
            RelativeLayout.LayoutParams(binding.line.layoutParams as RelativeLayout.LayoutParams)
        if (level == 1) {
            val line = Line(requireContext())
            line.setImageResource(R.drawable.line_image)
            line.layoutParams = params
            line.adjustViewBounds = true
            line.duration = Constants.SLOW
            binding.relativeLayout.addView(line)
            lines.add(line)
        } else {
            generateLines(params)
        }
    }

    private fun generateLines(par: RelativeLayout.LayoutParams) {
        if (level > 1) {
            val maxCount = if (level < 10) level else 10
            for (i in 1..maxCount) {
                val line = Line(requireContext())
                line.setImageResource(R.drawable.line_image)
                line.adjustViewBounds = true
                val params = RelativeLayout.LayoutParams(par)
                if (level < 5) {
                    params.width = (startWidth * 0.66).toInt()
                    line.duration = Constants.SLOW
                } else if (level < 10) {
                    params.width = (startWidth * 0.5).toInt()
                    line.duration = Constants.MEDIUM
                } else {
                    params.width = (startWidth * 0.4).toInt()
                    line.duration = Constants.FAST
                }
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                params.topMargin = Random.nextInt(0, 9) * Constants.STEP + Constants.MIN_TOP_MARGIN
                params.marginStart = DimensionConverter.dpToPixels(
                    Random.nextInt(
                        0,
                        (binding.container.width - params.width - 20).absoluteValue
                    ).toFloat(), requireContext()
                )
                line.layoutParams = params
                lines.add(line)
                binding.relativeLayout.addView(line)
            }
        }
    }

    private fun moveLine(line: Line) {
        line.lineAnimator?.removeAllUpdateListeners()
        var distance = 0f
        distance = if (line.direction == Direction.RIGHT) {
            binding.container.width.toFloat()
        } else {
            -binding.container.width.toFloat()
        }
        line.lineAnimator = ObjectAnimator.ofFloat(line, Constants.HORIZONTAL_MOVING, distance)
        line.lineAnimator?.interpolator = LinearInterpolator()
        line.lineAnimator?.duration = line.duration
        line.lineAnimator?.start()
        line.lineAnimator?.addUpdateListener {
            line.getLocationInWindow(line.lineLocation)
            if (line.direction == Direction.RIGHT && line.lineLocation[0] + line.width > binding.container.width - 10) {
                line.lineAnimator?.cancel()
                line.direction = Direction.LEFT
                moveLine(line)
            } else if (line.direction == Direction.LEFT && line.lineLocation[0] < 10) {
                line.lineAnimator?.cancel()
                line.direction = Direction.RIGHT
                moveLine(line)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun moveBottomRectangle() {
        binding.bottomRectangle.setOnTouchListener { v, event ->
            val x = event.rawX
            val lParams = v.layoutParams as ConstraintLayout.LayoutParams
            val topParams = binding.topRectangle.layoutParams as ConstraintLayout.LayoutParams
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    xDelta = x - lParams.leftMargin
                }
                MotionEvent.ACTION_MOVE -> {
                    val leftMargin = (x - xDelta).toInt()
                    if (leftMargin < binding.container.width - binding.topRectangle.width) {
                        lParams.leftMargin = leftMargin
                        topParams.leftMargin = leftMargin
                        v.layoutParams = lParams
                        binding.topRectangle.layoutParams = topParams
                    }
                }
                MotionEvent.ACTION_UP -> {
                    binding.topRectangle.getLocationInWindow(topRectangleLocation)
                }
            }
            true
        }
    }

    override fun onPause() {
        super.onPause()
        binding.relativeLayout.removeAllViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ballAnimator?.cancel()
        ballAnimator = null
        for (line in lines) {
            line.lineAnimator?.cancel()
            line.lineAnimator = null
        }
        callback?.isEnabled = false
        _binding = null
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val distanceX = e2!!.x - e1!!.x
        val distanceY = e2.y - e1.y
        Log.d("tag", "onFling called")
        if (distanceX.absoluteValue < distanceY.absoluteValue && distanceY <= 0) {
            //swipe up
            Log.d("tag", "swiped up")
            ballAnimator =
                ObjectAnimator.ofFloat(binding.ball, Constants.VERTICAL_MOVING, -ballDistance)
            ballAnimator?.interpolator = LinearInterpolator()
            ballAnimator?.duration = Constants.BALL_DURATION
            ballAnimator?.start()
            ballAnimator?.addUpdateListener {
                val ballLocation = IntArray(2)
                binding.ball.getLocationInWindow(ballLocation)
                if (isBallInsideLine(ballLocation)) {
                    Log.d("tag", "ball inside line")
                    cancelAnimators()
                    level = 1
                    binding.root.findNavController()
                        .navigate(R.id.action_levelFragment_to_tryAgainFragment)
                } else if (isLevelPassed(ballLocation)) {
                    Log.d("tag", "level passed")
                    cancelAnimators()
                    level++
                    val action = LevelFragmentDirections.actionLevelFragmentToYouWinFragment(level)
                    binding.root.findNavController().navigate(action)
                }
            }
        }
        return true
    }

    private fun cancelAnimators() {
        for (line in lines) {
            line.lineAnimator?.cancel()
        }
        ballAnimator?.cancel()
    }

    private fun isBallInsideLine(ballLocation: IntArray): Boolean {
        for (line in lines) {
            if (ballLocation[1] >= line.lineLocation[1] &&
                ballLocation[1] - line.lineLocation[1] <= line.height &&
                ballLocation[0] + binding.ball.width >= line.lineLocation[0] &&
                ballLocation[0] + binding.ball.width - line.lineLocation[0] <= line.width
            ) {
                return true
            }
        }
        return false
    }


    private fun isLevelPassed(ballLocation: IntArray): Boolean {
        return ballLocation[1] >= topRectangleLocation[1] &&
                ballLocation[1] - topRectangleLocation[1] <= binding.topRectangle.height &&
                ballLocation[0] >= topRectangleLocation[0] &&
                ballLocation[0] - topRectangleLocation[0] <= binding.topRectangle.width
    }


    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {}
}