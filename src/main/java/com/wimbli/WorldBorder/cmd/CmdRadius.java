package com.wimbli.WorldBorder.cmd;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;


public class CmdRadius extends WBCmd {
    public CmdRadius() {
        name = permission = "radius";
        hasWorldNameInput = true;
        minParams = 1;
        maxParams = 3;

        addCmdExample(nameEmphasizedW() + "<radiusX> [radiusZ] [maxLimit] - change radius.");
        helpText = "Using this command you can adjust the radius of an existing border. If [radiusZ] is not " +
                "specified, the radiusX value will be used for both. You can also optionally specify + or - at the start " +
                "of <radiusX> and [radiusZ] to increase or decrease the existing radius rather than setting a new value.";
    }

    @Override
    public void execute(CommandSender sender, Player player, List<String> params, String worldName) {
        if (worldName == null)
            worldName = player.getWorld().getName();

        BorderData border = Config.Border(worldName);
        if (border == null) {
            sendErrorAndHelp(sender, "This world (\"" + worldName + "\") must first have a border set normally.");
            return;
        }

        double x = border.getX();
        double z = border.getZ();
        double radiusX = border.getRadiusX();
        double radiusZ =  border.getRadiusZ();
        double maxRadius = -1;
        try {
            if (params.get(0).startsWith("+")) {
                // Add to the current radius
                radiusX += Integer.parseInt(params.get(0).substring(1));
            } else if (params.get(0).startsWith("-")) {
                // Subtract from the current radius
                radiusX -= Integer.parseInt(params.get(0).substring(1));
            } else
                radiusX = Integer.parseInt(params.get(0));

            if (params.size() >= 2) {
                if (params.get(1).startsWith("+")) {
                    // Add to the current radius
                    radiusZ += Integer.parseInt(params.get(1).substring(1));
                } else if (params.get(1).startsWith("-")) {
                    // Subtract from the current radius
                    radiusZ -= Integer.parseInt(params.get(1).substring(1));
                } else if (params.get(1).startsWith("max")) {
                    maxRadius = Integer.parseInt(params.get(1).substring(3));
                    radiusZ = radiusX;
                } else
                    radiusZ = Integer.parseInt(params.get(1));

                if (params.size() > 2 && params.get(2).startsWith("max"))
                    maxRadius = Integer.parseInt(params.get(2).substring(3));
            } else
                radiusZ = radiusX;
        } catch (NumberFormatException ex) {
            sendErrorAndHelp(sender, "The radius value(s) must be integers.");
            return;
        }

        if (maxRadius > 0 && border.getRadiusX() >= maxRadius) {
            sender.sendMessage("Radius is already at maximum.");
            return;
        }

        if (maxRadius > 0 && radiusX > maxRadius) {
            radiusX = maxRadius;
            sender.sendMessage("Radius reached maximum.");
        }

        Config.setBorder(worldName, (int) radiusX, (int)radiusZ, (int)x, (int)z);

        if (player != null)
            sender.sendMessage("Radius has been set. " + Config.BorderDescription(worldName));
    }
}
