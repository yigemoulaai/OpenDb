package Data_Manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractCache<T>  {
    private HashMap<Long, Boolean> getting;             // 正在获取某资源的线程
    private int maxResource;    // 缓存的最大缓存资源数
    private int count = 0;      // 缓存中元素的个数
    private Lock lock;
    private LRUCache cache;
    /* *
     * @description: 属性的构造
     * @params: [maxResource]
     * @return:
     */
    public AbstractCache() {
    }

    public AbstractCache(int maxResource){
        this.getting = new HashMap<Long, Boolean>();
        this.maxResource = maxResource;
        cache = new LRUCache<T>(16);
        this.lock = new ReentrantLock();
    }
    protected T get(long key) throws Exception {
        while (true){
            lock.lock();
            if(getting.containsKey(key)){
                lock.unlock();
                //休眠一秒 再次重试
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
                continue;
            }
            T obj= null;
            if(cache.get(key) != null){
                T o = (T)cache.get(key);
                lock.unlock();
                getting.put(key, true);
                return o;
            }
            // 当资源不在缓存中
                try {
                    obj = getForCache(key);
                    if(cache.getSize() == cache.getCapacity())
                    {
                        LRUCache.DLinkedNode node = cache.removeTail();
                        releaseForCache((T)node.value);
                    }
                   cache.put(key, obj);
                }catch (IOException e){
                    lock.lock();
                    getting.remove(key);
                    lock.unlock();
                    throw  e;
                }
                lock.lock();
                getting.remove(key);
                lock.unlock();
                return obj;
        }
    }
    /* *
     * @description: 服务关闭时刷盘到数据库
     * @params: []
     * @return: void
     */
    protected void close() {
        lock.lock();
        try {
            while (cache.getSize() != 0){
                LRUCache.DLinkedNode node = cache.removeTail();
                getting.remove(node.key);
                releaseForCache((T)node.value);
            }
        } finally {
            lock.unlock();
        }
    }

    /* *
      * @description:当资源不在缓存时的获取行为
      * @params: [key]
      * @return: T
      */
    protected abstract T getForCache(long key) throws Exception;

    /* *
     * @description: 资源被驱逐时的写回DB的行为
     * @params: [obj]
     * @return: void
     */
    protected abstract void releaseForCache(T obj); 

}

