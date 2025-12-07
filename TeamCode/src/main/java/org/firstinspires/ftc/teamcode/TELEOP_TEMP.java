package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.command.button.Button;
import com.arcrobotics.ftclib.command.button.GamepadButton;

import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.ButtonReader;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.gamepad.ToggleButtonReader;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

@TeleOp
public class TELEOP_TEMP extends LinearOpMode {
    Limelight3A limelight;

    MecanumDrive mecanum;
    Motor flywheel;

    Servo servo;

    PIDController autoAlignPID;

    public enum DEBUG_MODE
    {
        debug,
        regular
    }



    public void changeFlywheelSpeed()
    {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid() && result.getPipelineIndex() == 0) {
            double ty = result.getTy(); // How far up or down the target is (degrees)

            if (ty < -9.75d)
            {
                ty = -9.75;
            }

            // apriltag height = 38
            //limelight height = 10

            //Math.tan(Math.toradians(ty+10) = (38-11.5) / distance

            double distance = (38-11.5) / Math.tan(Math.toRadians(ty + 10));
            telemetry.addData("angle:", ty + 10);
            telemetry.addData("distance:", distance);
            double proportion = (distance / 200);
            telemetry.addData("proportion:", proportion);
            flywheel.set(ChangeNumbers.b + proportion * ChangeNumbers.m);
            telemetry.addData("speed:", flywheel.get());
        }
        else
        {
            flywheel.set(0.5f);
        }
    }

    public void autoAlign()
    {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid() && result.getPipelineIndex() == 0) {
            double tx = result.getTx(); // How far left or right the target is (degrees)
            telemetry.addData("autoalign angle", tx);

            double ty = result.getTy();
            if (ty < -9.75d) {
                ty = -9.75;
            }

            double distance = (38 - ChangeNumbers.limelightHeight) / Math.tan(Math.toRadians(ty + ChangeNumbers.limelightAngle));

            double targetAngle = Math.atan(distance / ChangeNumbers.distanceHorizontal);

            if (Math.abs(tx - targetAngle) > ChangeNumbers.tolerance)
            {
                double speed = autoAlignPID.calculate(tx, 0);
                mecanum.driveWithMotorPowers(-speed, -speed, -speed, -speed);
            }
            else
            {
                mecanum.driveWithMotorPowers(0, 0, 0, 0);
            }


        }
        else
        {
            mecanum.driveWithMotorPowers(0, 0, 0, 0);
        }
    }


    public enum STATE
    {
        driving,
        autoalign
    }

    STATE state;



    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        //12
        //30



        state = STATE.driving;

        waitForStart();
        autoAlignPID = new PIDController(ChangeNumbers.autoalign_P, ChangeNumbers.autoalign_I, ChangeNumbers.autoalign_D);

        if (ChangeNumbers.limelight)
        {
            limelight = hardwareMap.get(Limelight3A.class, "limelight");
            limelight.setPollRateHz(100);
            limelight.start();
        }


        GamepadEx driveOp = new GamepadEx(gamepad1);
        Button buttonB = new GamepadButton(driveOp, GamepadKeys.Button.RIGHT_BUMPER);

        servo = hardwareMap.get(Servo.class, "servo");
        flywheel = new Motor(hardwareMap, "flywheel");
        flywheel.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        Motor fl = new Motor(hardwareMap, "fl"); //3 // REAL = 0
        Motor bl = new Motor(hardwareMap, "bl"); // 1  // REAL = 1
        Motor fr = new Motor(hardwareMap, "fr"); //2 // REAL = 2 // V
        Motor br = new Motor(hardwareMap, "br"); // 0 // REAL = 2 // V

        ToggleButtonReader aReader = new ToggleButtonReader(
                driveOp, GamepadKeys.Button.A
        );

        ButtonReader bReader = new ButtonReader(
                driveOp, GamepadKeys.Button.B
        );

        ButtonReader upReader = new ButtonReader(
                driveOp, GamepadKeys.Button.DPAD_UP
        );

        ButtonReader downReader = new ButtonReader(
                driveOp, GamepadKeys.Button.DPAD_DOWN
        );

        mecanum = new MecanumDrive(
                fl,
                fr,
                bl,
                br
        );

        flywheel.set(0.5);


        while (opModeIsActive()) {

            bReader.readValue();

            if (ChangeNumbers.limelight)
            {
                changeFlywheelSpeed();

                if (bReader.isDown())
                {
                    telemetry.addData("wjp", "b");
                    state = STATE.autoalign;
                    autoAlignPID.reset();
                }
            }

            if (aReader.isDown())
            {
                telemetry.addData("pressig buttin", "Yes");
                servo.setPosition(0);
            }
            else
            {
                servo.setPosition(0.5);
            }

            //telemetry.addData("servo pasishan", servo.getPosition());

            if (upReader.wasJustReleased())
            {
                telemetry.addData("wjp", "up");
                autoAlignPID.setP(autoAlignPID.getP() + 0.001);
            }
            if (downReader.wasJustReleased())
            {
                telemetry.addData("wjp", "down");
                autoAlignPID.setP(autoAlignPID.getP() - 0.001);
            }
            telemetry.addData("autoAlign P", autoAlignPID.getP());



            telemetry.addData("lx", driveOp.getLeftX());
            telemetry.addData("ly", driveOp.getLeftY());
            telemetry.addData("rx", driveOp.getRightX());

            if (Math.abs(driveOp.getLeftX()) != 0 ||
                    Math.abs(driveOp.getLeftY()) != 0 ||
                    Math.abs(driveOp.getRightX()) != 0
            )
            {
                state = STATE.driving;
            }


            switch (state)
            {
                case driving:

                    mecanum.driveRobotCentric(
                            -driveOp.getLeftX(),
                            driveOp.getRightX(),
                            driveOp.getLeftY()
                    );
                    telemetry.addData("drive:", true);

                    break;
                case autoalign:

                    telemetry.addData("autoalign:", true);

                    autoAlign();
                    break;
                default:

            }
            telemetry.update();


        }
    }
}