package fred.exploregalore.util;

public interface EntityPrevPosAccess {

    double getPrevXServer();
    void setPrevXServer(double prevXServer);

    double getPrevYServer();
    void setPrevYServer(double prevYServer);

    double getPrevZServer();
    void setPrevZServer(double prevZServer);

    void savePrevPos();
}
