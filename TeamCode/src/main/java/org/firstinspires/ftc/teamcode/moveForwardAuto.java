package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.Drivebase;
import org.firstinspires.ftc.teamcode.subsystems.InOuttake;

@Autonomous
public class moveForwardAuto extends LinearOpMode {

    private InOuttake inOuttake;
    private Drivebase drivebase;





    @Override
    public void runOpMode() throws InterruptedException {

        waitForStart();

        inOuttake = new InOuttake(hardwareMap);

        drivebase = new Drivebase(hardwareMap);



        drivebase.simpleDrive(-0.2,1,0);

        sleep(1000);
        drivebase.simpleDrive(0,0,0);



    }
}
