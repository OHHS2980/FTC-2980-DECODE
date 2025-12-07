package org.firstinspires.ftc.teamcode.subsystems;


public void autoAlign() {

    LLResult result = limelight.getLatestResult();
    if (result != null && result.isValid() && result.getPipelineIndex() == 0) {
        double tx = result.getTx(); // How far left or right the target is (degrees)
        telemetry.addData("autoalign angle", tx);


        double ty = result.getTy(); // How far up or down the target is (degrees)


        if (ty < -9.75d) {
            ty = -9.75;


            double distance = (38 - cameraDistToGround) / Math.tan(Math.toRadians(ty + cameraAngle));

            double targetAngle = Math.atan(distance / camHorizontalDistToCenter);

            if (tx > targetAngle + tolerance) {
                double speed = autoAlignPID.calculate(tx, 0);
                mecanum.driveWithMotorPowers(speed, -speed, -speed, speed);
            }


        }
    }

}