public class WaveEditor {
	
	public void createWave(int wave) {
		
		switch (wave) {
		
		case 1:
			Board.alienManager.addAliens(1);
			break;
			
		case 2:
			Board.alienManager.addAliens(2);
			break;
			
		case 3:
			Board.alienManager.addAliens(3);
			break;
			
		case 4:
			Board.alienManager.addAliens(5);
			break;
			
		case 5:
			Board.purpBossManager.addPurpBoss();
			break;
			
		case 6:
			Board.alienManager.addAliens(7);
			break;
			
		case 7:
			Board.alienManager.addAliens(8);
			break;
			
		case 8:
			Board.purpBossManager.addPurpBoss();
			Board.alienManager.addAliens(1);
			break;
			
		case 9:
			Board.purpBossManager.addPurpBoss();
			Board.alienManager.addAliens(2);
			break;
			
		default:
			Board.currentState = Board.State.GAMEOVER;
		}
	}
}