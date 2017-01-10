import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Problem3 {

	public static void main(String[] args) {
		List<Zombie> zombiesList = readFile("input.txt");
		for (int i = 0, max = zombiesList.size(); i < max; i++) {
			System.out.println("Case #" + Integer.toString(i + 1) + ": " + String.format(java.util.Locale.US, "%.6f", zombiesList.get(i).getProbability()));
		}
	}
	
	public static List<Zombie> readFile (String fileName) {
		List<Zombie> zombiesList = new ArrayList<Zombie>();
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
		    for (int i = 0, nbZombies = Integer.parseInt(br.readLine()); i < nbZombies; i++) {	//For each zombie
		    	String line = br.readLine();
		    	int hp = Integer.parseInt(line.split(" ")[0]);
		    	line = br.readLine();
		    	String[] parts = line.split(" ");
		    	List<Spell> diceList = new ArrayList<Spell>();
		    	for (String s : parts) {	//For each dice
		    		String[] subParts = s.split("d");
		    		int diceNumber = Integer.parseInt(subParts[0]);
		    		int diceFaces;
		    		int diceModifier;
		    		int modifierIndex = subParts[1].indexOf("+");	//Is there a + symbol?
		    		if (modifierIndex == -1){
		    			modifierIndex = subParts[1].indexOf("-");	//Is there a - symbol?
		    		}
		    		if (modifierIndex != -1){
		    			diceFaces = Integer.parseInt(subParts[1].substring(0, modifierIndex));
		    			diceModifier = Integer.parseInt(subParts[1].substring(modifierIndex));
		    		} else {
		    			diceFaces = Integer.parseInt(subParts[1]);
		    			diceModifier = 0;
		    		}
		    		diceList.add(new Spell(diceNumber, diceFaces, diceModifier));
		    	}
		    	zombiesList.add(new Zombie(hp, diceList));
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return zombiesList;
	}
	
	public static class Zombie {
		public int hp;
		public List<Spell> spellList;
		
		public Zombie (int hp, List<Spell> spellList) {
			this.hp = hp;
			this.spellList = spellList;
		}
		
		public double getProbability() { //Returns the best spell's probability of killing the zombie
			double prob = 0;
			for (Spell s : spellList) {	//For each spell
				double temp = s.getProbability(hp); 
				if (temp > prob) {	//Check if its better
					prob = temp;
				}
				if (prob == 1) {
					return prob;
				}
			}
			return prob;
		}
	}
	
	public static class Spell {
		public int diceNumber;
		public int diceFaces;
		public int diceModifier;
		
		public Spell (int diceNumber, int diceFaces, int diceModifier) {
			this.diceNumber = diceNumber;
			this.diceFaces = diceFaces;
			this.diceModifier = diceModifier;
		}
		
		public int getMinDamage() {	//Returns this spell's minimum damage
			return diceNumber;
		}
		
		public int getMaxDamage() {	//Returns this spell's maximum damage
			return diceNumber * diceFaces;
		}
				
		public double getPossibilities() {	//Returns the possible number of dice results
			return Math.pow((double)diceFaces, (double)diceNumber);
		}
		
		public double getProbability(int hp) {	//Returns the probability of killing the zombie
			int hpTemp = hp - diceModifier;
			if (getMinDamage() >= hpTemp) {
				return 1;
			}
			if (getMaxDamage() < hpTemp) {
				return 0;
			}
			double probSum = 0;
			for (int p = hpTemp, max = getMaxDamage(); p <= max; p++) {
				for (int k = 0, limit = ((p - diceNumber) / diceFaces); k <= limit; k++) {
					int sign = (int) Math.pow(-1, k);
					double permutation = nChooseK(diceNumber, k);
					double correction = nChooseK(p - (diceFaces * k) - 1, diceNumber - 1);
					probSum += (sign * permutation * correction);
				}
			}
			return probSum / getPossibilities();
		}
	} 
	
	public static double nChooseK(int n, int k) {
        return factorial(n) / (factorial(k) * (factorial(n - k)));
    }
	
	public static double factorial(int n) {
        double fact = 1;
        for (int i = 1; i <= n; i++) {
            fact *= (double)i;
        }
        return fact;
    }
}
