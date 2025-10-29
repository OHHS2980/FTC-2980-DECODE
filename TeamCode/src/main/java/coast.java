import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp
public class coast extends OpMode {

    DcMotorEx Right2;
    DcMotorEx Right1;
    DcMotorEx Left1;
    DcMotorEx Left2;

    @Override
    public void init() {

        Left1 = hardwareMap.get(DcMotorEx.class, "left1");
        Left2 = hardwareMap.get(DcMotorEx.class, "left2");
        Right1 = hardwareMap.get(DcMotorEx.class, "right1");
        Right2 = hardwareMap.get(DcMotorEx.class, "right2");
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
        Right2.setVelocity(gamepad1.right_stick_x*1000);
        Right1.setVelocity(gamepad1.right_stick_x*-1000);


    }

    @Override
    public void stop() {
        Left1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Left2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Right1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Right2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



    }
}
