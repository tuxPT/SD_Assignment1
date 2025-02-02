package shared_regions;

import shared_regions.IArrivalTerminalExitPassenger;
import shared_regions.IGeneralRepository;
import common_infrastructures.SPassenger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MArrivalTerminalExit implements IArrivalTerminalExitPassenger {
    private IGeneralRepository MGeneralRepository;
    Integer CURRENT_NUMBER_OF_PASSENGERS;
    private Integer PLANE_PASSENGERS;
    ReentrantLock lock = new ReentrantLock(true);
    Condition lastPassenger = lock.newCondition();

    /**
     * @param PLANE_PASSENGERS number of passengers in a plane
     * @param MGeneralRepository The General Repository used for logging   
     */
    public MArrivalTerminalExit(Integer PLANE_PASSENGERS, MGeneralRepository MGeneralRepository)
    {        
        CURRENT_NUMBER_OF_PASSENGERS = 0;
        this.MGeneralRepository = MGeneralRepository;
        this.PLANE_PASSENGERS = PLANE_PASSENGERS;
    }


    public boolean addPassenger(Integer id, Integer current_Departure)
    {
        assert id != null : "Thread_id não especificado";
        lock.lock();
        boolean tmp = false;
        try {
            CURRENT_NUMBER_OF_PASSENGERS++;
            MGeneralRepository.updatePassenger(SPassenger.EXITING_THE_ARRIVAL_TERMINAL, id, null, null, null, false, null);
            if(current_Departure + CURRENT_NUMBER_OF_PASSENGERS == PLANE_PASSENGERS){
                tmp = true;
            }
            else{
                lastPassenger.await();
                tmp = false;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
        return tmp;

    }


    public Integer getCURRENT_NUMBER_OF_PASSENGERS() {
        lock.lock();
        Integer tmp = 0;
        try {
            tmp = CURRENT_NUMBER_OF_PASSENGERS;
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        finally {
            lock.unlock();
        }
        return tmp;
    }


    public void lastPassenger(){
        lock.lock();
        try{
            lastPassenger.signalAll();
            CURRENT_NUMBER_OF_PASSENGERS = 0;
        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        finally{
            lock.unlock();
        }
    }
}
