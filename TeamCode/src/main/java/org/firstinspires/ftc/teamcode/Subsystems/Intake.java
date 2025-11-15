package org.firstinspires.ftc.teamcode.Subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

//import com.arcrobotics.ftclib.command.SubsystemBase;
public class Intake extends SubsystemBase {
    public int motorCPR = 1000;

    private final DcMotor flywheelMotor;
    public Intake(HardwareMap hardwareMap)
    {
        flywheelMotor = hardwareMap.get(DcMotor.class, "Intake");
    }

    public void spinFlywheel()
    {
        flywheelMotor.setPower(1.0);
    }

    public void stopFlywheel()
    {
        flywheelMotor.setPower(0);
    }
}
