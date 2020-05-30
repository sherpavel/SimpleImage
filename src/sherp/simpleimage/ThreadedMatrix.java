package sherp.simpleimage;

class ThreadedMatrix {
    private final Thread[] matrixThreads;

    ThreadedMatrix(int threadsCount) {
        matrixThreads = new Thread[threadsCount];
    }

    void setTask(int matrixWidth, int matrixHeight, ThreadedTask task) {
        for (int t = 0; t < matrixThreads.length; t++) {
            int xStart = matrixWidth * t / matrixThreads.length;
            int xEnd = matrixWidth * (t+1) / matrixThreads.length;
            matrixThreads[t] = new Thread(() -> {
                for (int x = xStart; x < xEnd; x++) {
                    for (int y = 0; y < matrixHeight; y++)
                        task.matrixCell(x, y);
                }
            });
        }
    }

    void start() throws InterruptedException {
        for (Thread thread : matrixThreads)
            thread.start();

        for (Thread thread : matrixThreads)
            thread.join();
    }
}
