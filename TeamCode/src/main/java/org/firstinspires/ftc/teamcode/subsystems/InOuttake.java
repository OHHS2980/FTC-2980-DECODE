package org.firstinspires.ftc.teamcode.subsystems;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class InOuttake extends SubsystemBase{

    ServoEx flap;

    DcMotorEx outtake2;
    DcMotorEx intake;

    DcMotorEx outtake1;


    public InOuttake(final HardwareMap hMap, final String name){

        flap = hMap.get(SimpleServo.class, "flap");

       // flap = new SimpleServo(hardwareMap,"flap",-30,30);
        intake = hMap.get(DcMotorEx.class, "intake");
        outtake1 = hMap.get(DcMotorEx.class,"outtake1");
        outtake2 = hMap.get(DcMotorEx.class, "outtake2");
    }
}
