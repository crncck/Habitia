package com.example.habitapp;

import org.junit.Test;

import androidx.appcompat.widget.DrawableUtils;

import static org.junit.Assert.*;

public class CalculatePercentageTest {
        @Test
        public void percent_isCorrect() {

            float tar = 5;
            float enteredValue = 2;
            float percent = (enteredValue / tar) * 100;


            float actual = percent;
            float expected = 40;
            assertEquals("percentage calculation is failed",expected,actual,0.0001);
        }

}


