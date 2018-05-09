/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.googletrends.regression;

/**
 *
 * @author mou1609
 */
public class DataPoint {

        /**
         * the x value
         */
        public float x;

        /**
         * the y value
         */
        public float y;

        /**
         * Constructor.
         *
         * @param x the x value
         * @param y the y value
         */
        public DataPoint(float x, float y) {
                this.x = x;
                this.y = y;
        }
}
