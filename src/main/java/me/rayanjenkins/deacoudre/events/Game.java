package me.rayanjenkins.deacoudre.events;

/*
Notes :
- Choisir une couleur de laine
- Faire un instant de T de lancement de partie (rounds)
- Réécrire les fonctions + proprement (faire des méthodes, mettre un switch/case)
 */


import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.lang.model.type.NullType;
import java.util.ArrayList;

public class Game implements Listener, CommandExecutor  {

    private boolean isGameStarted = false;
    private Location jumpStart;
    private Location losersLoc;
    private ArrayList<String> players = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            System.out.println("Can't send this if not a player.");
        }
        if(cmd.getName().equalsIgnoreCase("deacoudre")) {
            Player op = (Player) sender;
            if (!(args.length == 0)) {
                if(isGameStarted) {
                    if(args[0].equals("end")) {
                        gameEnd();
                    }
                    if(args[0].equals("players")) {
                        showPlayers(op);
                    }
                    if(args[0].equals("leave")) {
                        if(players.contains(op.getDisplayName())) {
                            players.remove(op.getDisplayName());
                            Bukkit.broadcastMessage(ChatColor.RED + op.getDisplayName() + " a quitté la partie...");
                        } else {
                            op.sendMessage(ChatColor.RED + "Vous n'avez pas rejoint la partie, ou vous l'avez déjà quittée.");
                        }
                    }
                    if(args[0].equals("kick")) {
                        Player target = Bukkit.getServer().getPlayer(args[1]);
                        if(players.contains(target.getDisplayName())) {
                            players.remove(target.getDisplayName());
                            Bukkit.broadcastMessage(ChatColor.RED + target.getDisplayName() + " a été kick de la partie.");
                        } else {
                            op.sendMessage(ChatColor.RED + "Erreur :" +  target.getDisplayName() + " a déjà été kick.");
                        }
                    }
                    if(args[0].equals("add") || args[0].equals("join") || args[0].equals("start") || args[0].equals("setStart") || args[0].equals("setLosers")) {
                        op.sendMessage(ChatColor.RED + "ERREUR :" + ChatColor.AQUA + "Une partie est déjà lancée.");
                    }

                } else {
                    if(args[0].equals("start")) {
                        if(players.size() <= 1) {
                            op.sendMessage(ChatColor.RED + "Error :" + ChatColor.AQUA + "Impossible de lancer une partie, besoin de + d'un joueur.");
                        } else {
                            gameStart();
                        }
                    }
                    if(args[0].equals("setStart")) {
                        jumpStart = op.getLocation();
                        op.sendMessage(ChatColor.BLUE + "Emplacement de saut mis en place.");
                    }
                    if(args[0].equals("setLosers")) {
                        losersLoc = op.getLocation();
                        op.sendMessage(ChatColor.BLUE + "Emplacement du coin des losers mise en place.");
                    }
                    if(args[0].equals("add")) {

                        Player target = Bukkit.getServer().getPlayer(args[1]);
                        if(players.contains(target.getDisplayName())) {
                            op.sendMessage(ChatColor.RED + target.getDisplayName() + " a déjà rejoint la partie.");
                        } else {
                            players.add(target.getDisplayName());
                            Bukkit.broadcastMessage(ChatColor.GREEN + target.getDisplayName() + " a rejoint la partie !");
                        }
                    }
                    if(args[0].equals("kick")) {
                        Player target = Bukkit.getServer().getPlayer(args[1]);
                        if(players.contains(target.getDisplayName())) {
                            players.remove(target.getDisplayName());
                            Bukkit.broadcastMessage(ChatColor.RED + target.getDisplayName() + " a été kick de la partie.");
                        } else {
                            op.sendMessage(ChatColor.RED + "Erreur :" +  target.getDisplayName() + " a déjà été kick.");
                        }
                    }
                    if(args[0].equals("join")) {
                        if(players.contains(op.getDisplayName())) {
                            op.sendMessage(ChatColor.RED + "Vous avez déjà rejoint la partie.");
                        } else {
                            players.add(op.getDisplayName());
                            Bukkit.broadcastMessage(ChatColor.GREEN + op.getDisplayName() + " a rejoint la partie !");
                        }
                    }
                    if(args[0].equals("leave")) {
                        if(players.contains(op.getDisplayName())) {
                            players.remove(op.getDisplayName());
                            Bukkit.broadcastMessage(ChatColor.RED + op.getDisplayName() + " a quitté la partie...");
                        } else {
                            op.sendMessage(ChatColor.RED + "Vous n'avez pas rejoint la partie, ou vous l'avez déjà quittée.");
                        }
                    }
                    if(args[0].equals("players")) {
                        showPlayers(op);
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Erreur : Il manque un argument (/help DeACoudre)");
            }
        }
        return false;
    }

    @EventHandler
    public void onFall(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        for(String playerName : players) {
            if(playerName == player.getDisplayName()) {
                Location playerLoc = e.getPlayer().getLocation();
                Block block = playerLoc.getBlock().getRelative(BlockFace.DOWN);
                if(String.valueOf(block.getType()) == "WATER") {
                    playerLoc.setX(playerLoc.getX());
                    playerLoc.setY(playerLoc.getY());
                    playerLoc.setZ(playerLoc.getZ());
                    playerLoc.getBlock().setType(Material.GREEN_WOOL);

                    player.teleport(jumpStart);
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Bien joué " + player.getDisplayName() + " ! Au suivant !");
                }
                Material[] Wools = {Material.GREEN_WOOL};
                for(Material wool : Wools) {
                    if(String.valueOf(block.getType()).equals(String.valueOf(wool))) {
                        player.teleport(losersLoc);
                        players.remove(playerName);
                        if(players.size() == 1) {
                            Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "VICTOIRE DE " + player.getDisplayName() + ", PROSTERNEZ VOUS !");
                            gameEnd();
                        } else {
                            Bukkit.broadcastMessage(ChatColor.RED + "BOUUUUH, JUGEZ " + player.getDisplayName() + " ! Au suivant !");
                        }
                    }
                }
            }
        }
    }

    public void broadcast(String message) {
        for (String p : players) {
            Bukkit.getServer().getPlayer(p).sendMessage(message);
        }
    }

    public void gameStart() {
        isGameStarted = true;
        Bukkit.broadcastMessage(ChatColor.GREEN + "Début de la partie de Dé A Coudre !");
    }

    public void gameEnd() {
        isGameStarted = false;
        players.clear();
        Bukkit.broadcastMessage(ChatColor.GREEN + "Fin de la partie de Dé A Coudre !");
    }

    public void showPlayers(Player op) {
        op.sendMessage(ChatColor.GREEN + "Liste des joueurs : " + players.size());
        for(String player : players) {
            op.sendMessage(ChatColor.RED + player);
        }
    }
}
