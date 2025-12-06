package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class flywheel_tune extends LinearOpMode {


    public int delay = 1000;

    public Servo servo;
    Limelight3A limelight;

    Motor flywheel;

    public enum MOTIF
    {
        PPG,
        PGP,
        GPP
    }

    public MOTIF motif = MOTIF.PPG;
    //GPP
    public void airSort()
    {
        switch (motif)
        {
            case PPG:

                flywheel.set(1);

                servo.setPosition(0);

                sleep(delay);

                flywheel.set(0.35);



                break;
            case PGP:

                flywheel.set(0.35);

                servo.setPosition(0);

                sleep(delay / 2);

                flywheel.set(1);

                sleep(delay / 2);

                flywheel.set(0.35);

                break;
            case GPP:

                flywheel.set(0.35);

                servo.setPosition(0);

                break;
            default:

                break;
        }
    }
    public void changeFlywheelSpeed()
    {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid() && result.getPipelineIndex() == 0) {
            double ty = result.getTy(); // How far up or down the target is (degrees)

            // apriltag height = 30
            //limelight height = 10
            double distance = (20) / Math.tan(Math.toRadians(ty + 10));
            telemetry.addData("angle:", ty + 10);
            telemetry.addData("distance:", distance);
            double proportion = (distance / 200);
            telemetry.addData("proportion:", proportion);
            telemetry.addData("speed:", 0.35 + proportion * 0.25);


        }
    }

    @Override
    public void runOpMode() throws InterruptedException {

        flywheel = new Motor(hardwareMap, "flywheel");

        servo = hardwareMap.get(Servo.class, "servo");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.start();

        while (true)
        {
            if (gamepad1.leftBumperWasPressed())
            {
                airSort();
            }
            if (gamepad1.dpadUpWasPressed())
            {
                flywheel.set(flywheel.get() + 0.01);
            }
            if (gamepad1.dpadDownWasPressed())
            {
                flywheel.set(flywheel.get() - 0.01);
            }

            if (gamepad1.dpadLeftWasPressed())
            {
                delay += -100;
            }
            if (gamepad1.dpadRightWasPressed())
            {
                delay += 100;
            }
            telemetry.addData("speed:", flywheel.get());
            changeFlywheelSpeed();
            telemetry.update();
        }
    }
}