package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.List;

@Autonomous
public class AUTO_TEMP extends LinearOpMode {

    Limelight3A limelight;

    MecanumDrive mecanum;
    Motor flywheel;


    Servo servo;

    public enum MOTIF
    {
        PPG,
        PGP,
        GPP
    }
    public MOTIF motif = MOTIF.GPP;
    public void motif()
    {
        limelight.pipelineSwitch(1);

        while (true)
        {

            LLResult result = limelight.getLatestResult();
            if (result != null && result.getPipelineIndex() == 1)
            {
                List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                if (fiducialResults.stream().anyMatch(n -> n.getFiducialId() == 21))
                {
                    motif = MOTIF.GPP;
                    break;
                }
                if (fiducialResults.stream().anyMatch(n -> n.getFiducialId() == 22))
                {
                    motif = MOTIF.PGP;
                    break;
                }
                if (fiducialResults.stream().anyMatch(n -> n.getFiducialId() == 23))
                {
                    motif = MOTIF.PPG;
                    break;
                }
            }
        }
    }
    //gpp
    public void airSort()
    {
        switch (motif)
        {
            case PPG:

                flywheel.set(1);

                servo.setPosition(0);

                sleep(1000);

                flywheel.set(0.5);



                break;
            case PGP:

                flywheel.set(0.5);

                servo.setPosition(0);

                sleep(1000);

                flywheel.set(1);

                sleep(1000);

                flywheel.set(0.5);

                break;
            case GPP:

                flywheel.set(0.5);

                servo.setPosition(0);

                sleep(1000);

                flywheel.set(1);

                break;
            default:

                break;
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {


        waitForStart();

        Motor fl = new Motor(hardwareMap, "fl");
        Motor bl = new Motor(hardwareMap, "bl");
        Motor fr = new Motor(hardwareMap, "fr");
        Motor br = new Motor(hardwareMap, "br");

        servo = hardwareMap.get(Servo.class, "servo");
        flywheel = new Motor(hardwareMap, "flywheel");

        //Gamepad gamepad = new Gamepad(gamepad1);

        mecanum = new MecanumDrive(
                fl,
                fr,
                bl,
                br
        );

        //fr.setInverted(true);
        //br.setInverted(true);

        airSort();

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