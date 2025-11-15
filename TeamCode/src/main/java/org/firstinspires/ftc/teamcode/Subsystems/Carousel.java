package org.firstinspires.ftc.teamcode.Subsystems;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.arcrobotics.ftclib.command.Subsystem;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import java.util.List;
public class Carousel extends SubsystemBase {
    public DcMotor gearMotor;
    Limelight3A colorSensor;
    public int motorCPR = 288;
    private enum ball
    {
        purple,
        green,
        empty
    };
    public float[] carouselPID = {0.1f, 0.1f, 0.1f};
    public double PIDController(float P, float I, float D) {
        return 0;
    }
    private ball[] carousel = {ball.empty, ball.empty, ball.empty};
    // 0 - INTAKE SLOT
    // 1 - MID SLOT
    // 2 - OUTTAKE SLOT
    public void shiftFwd()
    {
        ball pos0 = carousel[0];
        ball pos1 = carousel[1];
        ball pos2 = carousel[2];

        carousel[0] = pos2;
        carousel[1] = pos0;
        carousel[2] = pos1;
    }


    public void rotateCarousel(int count)
    {
        int currentPosition = gearMotor.getTargetPosition();
        gearMotor.setTargetPosition(
                (
                    currentPosition
                    + (motorCPR / 3) * count
                )
                % motorCPR
        );
    }

    private void introduce(ball colorIntroduced)
    {
        carousel[0] = colorIntroduced;
    }
    private void pick(ball chosenColor)
    {
        ball intakeSlot = carousel[0];
        ball midSlot = carousel[1];
        ball outtakeSlot = carousel[2];

        if (outtakeSlot == chosenColor) {
            return;
        }
        else if (midSlot == chosenColor) {
            rotateCarousel(1);
        }
        else if (intakeSlot == chosenColor) {
            rotateCarousel(2);
        }
    }

    public Carousel(HardwareMap hardwareMap) {
        gearMotor = hardwareMap.get(DcMotor.class, "carouselMotor");
        colorSensor = hardwareMap.get(Limelight3A.class, "limelight");
        colorSensor.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        colorSensor.start(); // This tells Limelight to start looking!
        colorSensor.pipelineSwitch(0); // Switch to pipeline number 0
    }

    public boolean rotationFinished()
    {
        if (gearMotor.getCurrentPosition() == gearMotor.getTargetPosition())
        {
            return true;
        }
        return false;
    }

    public void checkIntake()
    {
        colorSensor.pipelineSwitch(0);
        LLResult resultGreen = colorSensor.getLatestResult();
        if (resultGreen != null && resultGreen.isValid()) {
            List<LLResultTypes.ColorResult> colorTargets = resultGreen.getColorResults();
            for (LLResultTypes.ColorResult colorTarget : colorTargets) {
                double area = colorTarget.getTargetArea(); // size (0-100)
                if (area > 50) {
                    carousel[0] = ball.green;
                }
            }
        }

        colorSensor.pipelineSwitch(1);
        LLResult resultPurple = colorSensor.getLatestResult();
        if (resultPurple != null && resultPurple.isValid()) {
            List<LLResultTypes.ColorResult> colorTargets = resultPurple.getColorResults();
            for (LLResultTypes.ColorResult colorTarget : colorTargets) {
                double area = colorTarget.getTargetArea(); // size (0-100)
                if (area > 50) {
                    carousel[0] = ball.purple;
                }
            }
        }
    }

    @Override
    public void periodic() {
        super.periodic();
        checkIntake();
    }
}