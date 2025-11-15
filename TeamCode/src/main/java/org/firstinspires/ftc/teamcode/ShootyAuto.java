package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous
public class ShootyAuto extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {


        waitForStart();

        Servo pushServo = hardwareMap.get(Servo.class, "servo");

        Motor flywheel = new Motor(hardwareMap, "flywheel");

        Motor fl = new Motor(hardwareMap, "fl");
        Motor bl = new Motor(hardwareMap, "bl");
        Motor fr = new Motor(hardwareMap, "fr");
        Motor br = new Motor(hardwareMap, "br");

        flywheel.set(0.5);

        fl.set(.65);
        fr.set(.65);

        bl.set(.65);
        br.set(.65);

        sleep(3300);

        fl.set(0);
        fr.set(0);

        bl.set(0);
        br.set(0);

        pushServo.setPosition(0);






    }
}
