package com.example.carracegame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs

class GameView(private var context: Context, private var gameTask: GameTask) : View(context) {
    private var myPaint: Paint = Paint()
    private var speed = 1
    private var time = 0
    private var score = 0
    private var myCarPosition = 1 // Start in the middle lane
    private val otherCars = ArrayList<HashMap<String, Any>>()

    private var viewWidth = 0
    private var viewHeight = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewWidth = width
        viewHeight = height

        if (time % 700 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time // Set the start time for the new car
            otherCars.add(map)
        }
        time += 10 + speed

        // Draw the red car
        val carWidth = viewWidth / 5
        val carHeight = viewHeight / 5
        val redCarX = myCarPosition * viewWidth / 3 + viewWidth / 15
        val redCarY = viewHeight - carHeight
        val redCarDrawable = ContextCompat.getDrawable(context, R.drawable.red_car)
        redCarDrawable?.setBounds(redCarX, redCarY, redCarX + carWidth, redCarY + carHeight)
        redCarDrawable?.draw(canvas)

        // Draw other cars
        val iterator = otherCars.iterator()
        while (iterator.hasNext()) {
            val car = iterator.next()
            val carX = car["lane"] as Int * viewWidth / 3 + viewWidth / 15
            var carY = (time - car["startTime"] as Int)
            val yellowCarDrawable = ContextCompat.getDrawable(context, R.drawable.yellow_car)
            yellowCarDrawable?.setBounds(carX, carY, carX + carWidth, carY + carHeight)
            yellowCarDrawable?.draw(canvas)

            if (abs(carY - redCarY) < carHeight && car["lane"] as Int == myCarPosition) {
                // Collision detected
                gameTask.closeGame(score)
            }
            if (carY > viewHeight) {
                // Remove car if it goes beyond the screen
                iterator.remove()
                score++
                speed = 1 + abs(score / 8)
            }
        }

        // Draw score and speed
        myPaint.color = Color.WHITE
        myPaint.textSize = 40f
        canvas.drawText("Score: $score", 20f, 80f, myPaint)
        canvas.drawText("Speed: $speed", 20f, 150f, myPaint)
        invalidate()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val laneWidth = viewWidth / 3
                myCarPosition = when {
                    event.x < laneWidth -> 0 // Left lane
                    event.x < 2 * laneWidth -> 1 // Middle lane
                    else -> 2 // Right lane
                }
                invalidate()
            }
        }
        return true
    }
}
