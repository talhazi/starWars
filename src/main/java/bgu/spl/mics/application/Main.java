package bgu.spl.mics.application;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.LandoMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Vector;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {

	public static void main(String[] args) {
		final String filepath = args[0];
		Diary diary = Diary.getInstance();
		diary.SetStartProgram();
		Gson gson = new Gson();
		try {
			Input our = gson.fromJson(new FileReader(filepath), Input.class);
			Attack[] attacks = our.getAttacks();
			long r2d2 = our.getR2D2();
			long lando = our.getLando();
			int ewoks = our.getEwoks();

			Ewok[] EwokArray=new Ewok[ewoks+1];
			for (int i=1; i<=ewoks; i++){
				Ewok newEwok = new Ewok(i, true);
				EwokArray[i]=newEwok;
			}
			EwokArray[0]=new Ewok (999,false); // garbage

			Vector<Thread> threads = new Vector<>();

			//Loading MicroServices
			MicroService LeiaMicro = new LeiaMicroservice(attacks);
			LeiaMicro.getEwoks().loadEwok(EwokArray);
			Thread Leia = new Thread(LeiaMicro);
			Leia.setName("Leia");

			MicroService C3POMicro = new C3POMicroservice();
			Thread C3PO = new Thread(C3POMicro);
			C3PO.setName("C3PO");
			C3PO.start();
			threads.add(C3PO);

			MicroService HanSoloMicro = new HanSoloMicroservice();
			Thread HanSolo = new Thread(HanSoloMicro);
			HanSolo.setName("HanSolo");
			HanSolo.start();
			threads.add(HanSolo);

			while (true)
			{
				boolean y=C3POMicro.getConfrimInitialization();
				boolean z=HanSoloMicro.getConfrimInitialization();
				if (y&z)
				{	Leia.start();
					threads.add(Leia);
					break;
				}
				else
					synchronized (diary.getIndicator())
					{diary.getIndicator().wait();}
			}

			R2D2Microservice R2D2Micro = new R2D2Microservice(r2d2);
			Thread R2D2 = new Thread(R2D2Micro);
			R2D2.setName("R2D2");
			R2D2.start();
			threads.add(R2D2);

			LandoMicroservice LandoMicro = new LandoMicroservice(lando);
			Thread Lando = new Thread(LandoMicro);
			Lando.setName("Lando");
			Lando.start();
			threads.add(Lando);

			for (Thread x:threads)
				x.join();

			diary.printOutput(args[1]);
		}

		catch (FileNotFoundException | InterruptedException ex) {
			ex.printStackTrace();
		}
	}
}
