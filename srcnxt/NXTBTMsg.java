import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * NXTBTMsg is shared between robustpc and robustnxt. Please keep the file identical 
 * in both modules
 * 
 * @author Konrad Kulakowski
 */
public class NXTBTMsg {
	public NXTBTMsg() {}
	
	public NXTBTMsg(int methodUID, byte methodType, int[] args) {
		this.methodType = methodType;
		this.methodUID = methodUID;
		this.args = args;
	}
	
	public NXTBTMsg(int methodUID, byte methodType, int arg1) {
		this.methodType = methodType;
		this.methodUID = methodUID;
		this.args = new int[1];
		args[0] = arg1;
	}	
	
	public NXTBTMsg(int methodUID, byte methodType, String args) {
		this.methodType = methodType;
		this.methodUID = methodUID;
		this.args = new int[args.length()];
		for (int i = 0; i < args.length(); i++) {
			this.args[i] = (int)args.charAt(i);
		}
	}
	
	public String getMsg() {
		char[] c = new char[args.length];
		for (int i = 0; i < args.length; i++) {
			c[i] = (char)args[i];
		}
		return new String(c);
	}

	public void fromStream(DataInputStream stream) {
		try {
			this.methodUID = stream.readInt();
			this.methodType = stream.readByte();
			int argsNumber = stream.readInt();
			if (argsNumber > 0) {
				this.args = new int[argsNumber];
				for (int i = 0; i < argsNumber; i++) {
					args[i] = stream.readInt();
				}
			}
		} catch (IOException e) {
		}
	}

	public void toStream(DataOutputStream stream) {
		try {
			stream.writeInt(methodUID);
			stream.flush();
			stream.writeByte(methodType);
			stream.flush();
			stream.writeInt(args == null ? (int) 0 : (int) args.length);
			stream.flush();
			for (int i = 0; args != null && i < args.length; i++) {
				stream.writeInt(args[i]);
				stream.flush();
			}
		} catch (IOException e) {
		}
	}

	public int methodUID;
	public byte methodType;
	public int[] args;
}