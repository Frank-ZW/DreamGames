package net.craftgalaxy.minigamecore.socket;

import net.craftgalaxy.minigamecore.MinigameCore;
import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;
import net.craftgalaxy.minigameservice.packet.client.PacketPlayOutConfirmDisconnect;
import net.craftgalaxy.minigameservice.packet.server.PacketPlayInServerConnect;
import net.craftgalaxy.minigamecore.socket.manager.CoreManager;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketWrapper implements Runnable {

    private final MinigameCore plugin;
    private final Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public SocketWrapper(MinigameCore plugin) throws IOException {
        this.plugin = plugin;
        this.socket = new Socket(plugin.getHostName(), plugin.getPort());
    }

    public void sendPacket(@NotNull MinigamePacketPlayIn packet) throws IOException {
        this.output.writeObject(packet);
        this.output.flush();
    }

    public boolean isConnected() {
        return this.socket.isConnected();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            this.output = new ObjectOutputStream(this.socket.getOutputStream());
            this.input = new ObjectInputStream(this.socket.getInputStream());
            this.sendPacket(new PacketPlayInServerConnect(plugin.getServerName()));
            while (true) {
                Object object = this.input.readObject();
                if (object instanceof PacketPlayOutConfirmDisconnect) {
                    break;
                } else {
                    CoreManager.getInstance().handlePacket(object);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!(e instanceof EOFException)) {
                e.printStackTrace();
            }
        } finally {
            try {
                this.output.close();
                this.input.close();
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
