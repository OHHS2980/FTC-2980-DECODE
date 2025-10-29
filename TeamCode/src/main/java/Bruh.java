import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
public class Bruh extends OpMode {

    DcMotorEx Rightz;
    DcMotorEx Righto;
    DcMotorEx Leftz;
    DcMotorEx Lefto;

    @Override
    public void init() {
        Leftz = hardwareMap.get(DcMotorEx.class, "leftz");
        Lefto = hardwareMap.get(DcMotorEx.class, "lefto");
        Rightz = hardwareMap.get(DcMotorEx.class, "rightz");
        Righto = hardwareMap.get(DcMotorEx.class, "righto");

        // IMPORTANT: Set to run without encoder mode first
        Leftz.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Lefto.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Rightz.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Righto.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        Leftz.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Lefto.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Rightz.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Righto.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop(){
        telemetry.addData("leftz", Leftz.getCurrentPosition());
        telemetry.addData("lefto", Lefto.getCurrentPosition());
        telemetry.addData("rightz", Rightz.getCurrentPosition());
        telemetry.addData("righto", Righto.getCurrentPosition());

        Lefto.setPower(0.5);
        Leftz.setPower(-0.5);
        Righto.setPower(0.5);
        Rightz.setPower(-0.5);

        telemetry.update();
    }
}