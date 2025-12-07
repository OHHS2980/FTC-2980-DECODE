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




    public DcMotorEx outtake1;
    DcMotorEx intake2;




    public InOuttake(HardwareMap hMap){

        outtake1 = hMap.get(DcMotorEx.class,"outtake1");

        outtake2 = hMap.get(DcMotorEx.class, "outtake2");

        intake = hMap.get(DcMotorEx.class, "intake");

        intake2 = hMap.get(DcMotorEx.class, "intake2");

    }

    public void runOuttake(double power){

        outtake1.setPower(power);

        outtake2.setPower(power * -1);

        intake.setPower(power * -1);
    }

    public void runBelt(double power){

        intake2.setPower(power);

    }

    public void stopOuttake(){
        outtake1.setPower(0);

        intake.setPower(0);

        intake2.setPower(0);
    }

    public void stopBelt(){

        outtake2.setPower(0);
    }
}