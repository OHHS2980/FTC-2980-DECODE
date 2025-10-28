import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.arcrobotics.ftclib.geometry.Vector2d;
import com.arcrobotics.ftclib.hardware.RevIMU;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PwmControl;


import org.ejml.equation.Variable;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp
public class TeleOp extends OpMode {

    // Declare hardware variables here

    MotorEx Rightz;
    MotorEx Righto;

    MotorEx Leftz;

    MotorEx Lefto;
    PIDFController pid;
    IMU IMU;

    @Override
    public void init() {

        // Initialize hardware here
        //Hardware
                Leftz = new MotorEx(hardwareMap, "leftz");
                //MotorEx One = new Motor(hardwareMap, "o");
                Lefto = new MotorEx(hardwareMap, "lefto");

                Rightz = new MotorEx(hardwareMap, "rightz");

                Righto = new MotorEx(hardwareMap, "righto");


        //Software
        pid=new PIDFController(7, 0, 0, 1);

        //IMU stuff




    }

    @Override
    public void loop() {
        Vector2d Leftjoy = new Vector2d(gamepad1.left_stick_x,-gamepad1.left_stick_y);
        Vector2d Rightjoy = new Vector2d(gamepad1.right_stick_x,- gamepad1.right_stick_y);
        // Example with a motor's current position
        float targetPosition = 100;
        double velocityR0 = pid.calculate(targetPosition, Rightz.getVelocity());
        double velocityR1 = pid.calculate(targetPosition, Righto.getVelocity());
        double velocityL0 = pid.calculate(targetPosition, Leftz.getVelocity());
        double velocityL1 = pid.calculate(targetPosition, Lefto.getVelocity());
        //double velocity = Leftz.getVelocity();

        //TeleOp control logic goes here
        //
        //Rightz.setVelocity(velocityR0);
        Righto.setVelocity(velocityR1);
        Leftz.setVelocity(velocityL0);
        Lefto.setVelocity(velocityL1);
        //One.setPower(command);
        //Zero.set(gamepad1.right_stick_y);
        //One.setPower((-gamepad1.right_stick_y));
        telemetry.addData("Leftz Velocity", Leftz.getVelocity());
        telemetry.addData("Lefto Velocity", Lefto.getVelocity());
        telemetry.addData("Difference", (Leftz.getCurrentPosition()-Lefto.getCurrentPosition()));
        telemetry.addData("Oh Yeah!", Leftjoy);
        //telemetry.addData("velocity", Righto.getVelocity());
        //telemetry.addData("ticks per second", );



    }

    @Override
    public void stop() {
        // Optional: Code to execute when the OpMode stops
        // test commit michi
    }
}