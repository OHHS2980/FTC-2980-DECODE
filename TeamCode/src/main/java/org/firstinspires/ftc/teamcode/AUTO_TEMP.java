package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Autonomous
public class AUTO_TEMP extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {


        waitForStart();

        Motor fl = new Motor(hardwareMap, "fl");
        Motor bl = new Motor(hardwareMap, "bl");
        Motor fr = new Motor(hardwareMap, "fr");
        Motor br = new Motor(hardwareMap, "br");



        //Gamepad gamepad = new Gamepad(gamepad1);

        MecanumDrive mecanum = new MecanumDrive(
                fl,
                fr,
                bl,
                br
        );

        fr.setInverted(true);
        br.setInverted(true);

        fl.set(1);
        fr.set(1);

        bl.set(-1);
        br.set(-1);

        sleep(1000);

        fl.set(0);
        fr.set(0);

        bl.set(0);
        br.set(0);
    }
}