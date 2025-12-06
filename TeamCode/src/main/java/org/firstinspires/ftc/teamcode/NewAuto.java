package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ChangeNumbers;

@Autonomous (name = "newAuto")
public class NewAuto extends LinearOpMode {

    public DcMotor carouselMotor;

    boolean fullyDone = false;

    int done = 0;


    public IMU imu;

    public DcMotor outtakeMotorA;

    public DcMotor outtakeMotorB;

    private Motor front_left  = null;
    private Motor front_right = null;
    private Motor back_left   = null;
    private Motor back_right  = null;









    @Override
    public void runOpMode() {



        waitForStart();

        front_left   = new Motor(hardwareMap, "leftFront");
        front_right   = new Motor(hardwareMap, "rightFront");
        back_left    = new Motor(hardwareMap, "leftBack");
        back_right  = new Motor(hardwareMap, "rightBack");
        imu = hardwareMap.get(IMU.class, "imu");

        back_right.setInverted(true);
        front_right.setInverted(true);

        Servo pushServo = hardwareMap.get(Servo.class, "servo");

        pushServo.setPosition(0.5);
        front_left.set(-ChangeNumbers.speed);
        front_right.set(-ChangeNumbers.speed);
        back_left.set(-ChangeNumbers.speed);
        back_right.set(-ChangeNumbers.speed);
        sleep(ChangeNumbers.time);
        front_left.set(0);
        front_right.set(0);
        back_left.set(0);
        back_right.set(0);
        pushServo.setPosition(0);


        while (opModeIsActive()){


        }


        //front_left   = new Motor(hardwareMap, "leftFront");
        //front_right   = new Motor(hardwareMap, "rightFront");
        ///back_left    = new Motor(hardwareMap, "leftBack");
        ///back_right  = new Motor(hardwareMap, "rightBack");

        //front_right.setInverted(true);
        ///back_right.setInverted(true);

        //moveForward();


    }

}
