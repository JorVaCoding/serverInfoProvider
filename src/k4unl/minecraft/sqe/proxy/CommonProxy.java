package k4unl.minecraft.sqe.proxy;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import k4unl.minecraft.sqe.lib.Log;
import k4unl.minecraft.sqe.network.rcon.RConThreadQuery;
import net.minecraft.network.rcon.RConThreadBase;
import net.minecraft.server.dedicated.DedicatedServer;

import java.lang.reflect.Field;

/**
 * @author Koen Beckers (K-4U)
 */
public class CommonProxy {

    @SideOnly(Side.SERVER)
    public void serverStarted(FMLServerStartingEvent event){
        Log.info("Server starting");
        if (event.getServer() instanceof DedicatedServer) {
            DedicatedServer theServer = (DedicatedServer) event.getServer();

            try {
                Field thread = ReflectionHelper.findField(theServer.getClass(), "theRConThreadQuery", "field_71342_m");
                if (theServer.getBooleanProperty("enable-query", false)) {
                    Field isRunning = ReflectionHelper.findField(RConThreadBase.class, "running", "field_72619_a");
                    Log.info("Disabling vanilla query listener");
                    isRunning.setBoolean(thread.get(theServer), false);
                }
                //Possibly that we need to wait a while before starting the new thread here..
                RConThreadQuery theNewThread = new RConThreadQuery(theServer);
                Log.info("Starting Extended Query Listener");
                thread.set(theServer, theNewThread);
                theNewThread.startThread();
            } catch (IllegalAccessException e) {
                Log.error("Error during reflection of theRConThreadQuery: ");
                e.printStackTrace();
            }

            if (theServer.getBooleanProperty("enable-rcon", false)) {
                /*field_155771_h.info("Starting remote control listener");
                this.theRConThreadMain = new RConThreadMain(theServer);
                this.theRConThreadMain.startThread();*/
            }
        }
    }
}