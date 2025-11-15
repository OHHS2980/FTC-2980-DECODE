package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Outtake {

    private final DcMotor flywheelMotor1;
    private final DcMotor flywheelMotor2;

    public Outtake(HardwareMap hardwareMap)
    {
        flywheelMotor1 = hardwareMap.get(DcMotor.class, "Outtake1");
        flywheelMotor2 = hardwareMap.get(DcMotor.class, "Outtake2");

    }

    public void spinFlywheel()
    {
        flywheelMotor1.setPower(1.0);
        flywheelMotor2.setPower(1.0);
    }

    public void stopFlywheel()
    {
        flywheelMotor1.setPower(0);
        flywheelMotor2.setPower(0);
    }
}
