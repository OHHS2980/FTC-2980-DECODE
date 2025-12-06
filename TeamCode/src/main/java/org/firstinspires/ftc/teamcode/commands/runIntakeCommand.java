
// this isn't in use. Ignore it pls- michi
package org.firstinspires.ftc.teamcode.commands;


import com.arcrobotics.ftclib.command.CommandBase;
import org.firstinspires.ftc.teamcode.subsystems.InOuttake;

public class runIntakeCommand extends CommandBase {

    private InOuttake subsystem;

    public runIntakeCommand(InOuttake subsystem) {
        this.subsystem = subsystem;

        addRequirements(subsystem);
    }

    @Override
    public  void initialize() {
    subsystem.runIntake(10);
    }

}


