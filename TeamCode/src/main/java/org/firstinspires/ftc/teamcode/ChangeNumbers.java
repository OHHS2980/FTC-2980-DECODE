package org.firstinspires.ftc.teamcode;

public class ChangeNumbers {

    //Auto Align Control
    // P -- If you want the auto-align to go faster if the angle is farther away, increase this

    public float delay = 1000;


    //flywheel speed = m(distance) + b
    public static float b = 0.4725f; //.4525

    public static float m = 0.225f; //.225

    public float highSpeed = 1;

    //AUTO ALIGN TUNING

    public static double tolerance = 0.05;
    //if autoalign is within this angle, stop it so it stops moving

    //If you want the auto align to go faster if its farther away, increase this
    //It may overshoot if P is too big
    public static double autoalign_P = 0.05;

    // I -- If you want the auto-align to go faster when it's taking too long, increase this
    public static double autoalign_I = 0.00;

    // D -- If you want the auto-align to go slower if it's rapidly approaching the correct angle
    public static double autoalign_D = 0.01;

    //Auto Drive
    // If you need the robot to go faster during auto, change this
    //0 = no move, 1 = max speed
    public static double speed = 0.5;
    //If you need the robot to drive for longer during auto, change this (time in seconds)
    public static long time = 2;

   //Auto flywheel changing
    // Final equation for flywheel (1 is max) is  = base + distance proportion * distanceWeight
    //Distance proportion is distance from apriltag ( in inches ) / 200 inches
    public static double distanceWeight = 0.5;
    public static double base = 0.5;

    public static boolean limelight = true;



}
