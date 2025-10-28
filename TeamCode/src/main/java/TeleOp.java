import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp
public class TeleOp extends OpMode {

    // Declare hardware variables here

    MotorEx Zero;
    MotorEx One;
    PIDFController pid;


    @Override
    public void init() {

        // Initialize hardware here
        //Hardware
                One = new MotorEx(hardwareMap, "o");
                //MotorEx One = new Motor(hardwareMap, "o");
                Zero = new MotorEx(hardwareMap, "z");
                Zero.setRunMode(Motor.RunMode.VelocityControl);

        //Software
        pid=new PIDFController(7, 0, 0, 1);



    }

    @Override
    public void loop() {
        // Example with a motor's current position
        //float targetPosition = 100;
        //double command = pid.calculate(targetPosition, Zero.getCurrentPosition());
        //double velocity = Zero.getVelocity();

        // Your TeleOp control logic goes here
        // This code will execute repeatedly during the TeleOp period
        //Zero.setVelocity(command);
        //One.setPower(command);
        //Zero.set(gamepad1.right_stick_y);
        //One.setPower((-gamepad1.right_stick_y));
        //telemetry.addData("Zero pos", Zero.getCurrentPosition());
        //telemetry.addData("One pos", One.getCurrentPosition());
        //telemetry.addData("Difference", (Zero.getCurrentPosition()-One.getCurrentPosition()));
        telemetry.addData("velocity", Zero.getVelocity());
        //telemetry.addData("ticks per second", );



    }

    @Override
    public void stop() {
        // Optional: Code to execute when the OpMode stops
    }
}