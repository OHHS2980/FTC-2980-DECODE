package org.firstinspires.ftc.teamcode.subsystems;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;


public class InOuttake extends SubsystemBase{

    ServoEx flap;

     DcMotorEx outtake2;
    DcMotorEx intake;



     DcMotorEx outtake1;

    // flap = new SimpleServo(hardwareMap,"flap",-30,30);


    public InOuttake(HardwareMap hMap){

        outtake1 = hMap.get(DcMotorEx.class,"outtake1");
        outtake2 = hMap.get(DcMotorEx.class, "outtake2");

    }

    public void runOuttake(double power){
        outtake1.setPower(power);
        outtake2.setPower(-power);
    }

    public void stopOuttake(){
        outtake1.setPower(0);
        outtake2.setPower(0);
    }




}
