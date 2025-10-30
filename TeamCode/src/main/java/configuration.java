import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.arcrobotics.ftclib.hardware.motors.CRServo;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

@TeleOp
public class configuration extends OpMode {

    DcMotorEx Right2;
    DcMotorEx Right1;
    DcMotorEx Left1;
    DcMotorEx Left2;

    //ServoEx intakeL = new SimpleServo(hardwareMap, "",0,360) {

    //ServoEx intakeR = new SimpleServo(hardwareMap, "",0,360) {


    CRServo intakeL;
    CRServo intakeR;
    ServoEx flap;

    Motor outtakeL = new Motor(hardwareMap, "");

    Motor outtakeR= new Motor(hardwareMap, "");



    @Override
    public void init() {

        Left1 = hardwareMap.get(DcMotorEx.class, "left1");
        Left2 = hardwareMap.get(DcMotorEx.class, "left2");
        Right1 = hardwareMap.get(DcMotorEx.class, "right1");
        Right2 = hardwareMap.get(DcMotorEx.class, "right2");
        intakeL = new CRServo(hardwareMap,"");
        intakeR = new CRServo(hardwareMap,"");
        flap = new SimpleServo(hardwareMap,"",0,15);
    }

    @Override
    public void loop() {
        Left1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Left2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Right1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Right2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        Left2.setVelocity(gamepad1.left_stick_y*1000);
        Left1.setVelocity(gamepad1.left_stick_y*1000);
        Right2.setVelocity(gamepad1.left_stick_y*1000);
        Right1.setVelocity(gamepad1.left_stick_y*1000);


        Left2.setVelocity(gamepad1.right_stick_y*500);
        Left1.setVelocity(gamepad1.right_stick_y*-500);
        Right2.setVelocity(gamepad1.right_stick_x*500);
        Right1.setVelocity(gamepad1.right_stick_x*-500);


        double outtake_p = (0.5);
        if(gamepad1.dpad_up) {

            outtake_p = (outtake_p + 0.1);
        }
        if(gamepad1.a)
            flap.rotateByAngle(15);

        outtakeL.motor.setPower(outtake_p);
        outtakeR.motor.setPower(-outtake_p);
        intakeL.set(1);
        intakeR.set(1);

    }

    @Override
    public void stop() {
        Left1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Left2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Right1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Right2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



    }
}
