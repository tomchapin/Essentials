package net.ess3.commands;

import lombok.Cleanup;
import static net.ess3.I18n._;
import net.ess3.api.ISettings;
import net.ess3.api.IUser;
import net.ess3.permissions.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandtpall extends EssentialsCommand
{
	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			if (sender instanceof Player)
			{
				teleportAllPlayers(sender, ess.getUserMap().getUser((Player)sender));
				return;
			}
			throw new NotEnoughArgumentsException();
		}

		final IUser player = ess.getUserMap().matchUser(args[0], false, false);
		teleportAllPlayers(sender, player);
	}

	private void teleportAllPlayers(CommandSender sender, IUser user)
	{
		sender.sendMessage(_("teleportAll"));
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final IUser player = ess.getUserMap().getUser(onlinePlayer);
			if (user == player)
			{
				continue;
			}
			@Cleanup
			ISettings settings = ess.getSettings();
			settings.acquireReadLock();

			if (user.getPlayer().getWorld() != player.getPlayer().getWorld() && settings.getData().getGeneral().isWorldTeleportPermissions()
				&& !Permissions.WORLD.isAuthorized(user, user.getPlayer().getWorld().getName()))
			{
				continue;
			}
			try
			{
				player.getTeleport().now(user.getPlayer(), false, TeleportCause.COMMAND);
			}
			catch (Exception ex)
			{
				ess.getCommandHandler().showCommandError(sender, commandName, ex);
			}

		}
	}
}