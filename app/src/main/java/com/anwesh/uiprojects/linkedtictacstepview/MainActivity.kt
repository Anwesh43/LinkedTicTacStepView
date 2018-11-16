package com.anwesh.uiprojects.linkedtictacstepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.tictacstepview.TicTacStepView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TicTacStepView.create(this)
    }
}
