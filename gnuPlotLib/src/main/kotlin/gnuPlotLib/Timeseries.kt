package gnuPlotLib

import java.util.*

class Timeseries(val capacity : Int) : ArrayDeque<Double>(capacity) {

    override fun add(x : Double) : Boolean {
        super.add(x)
        if(size > capacity) {
            pollFirst()
        }
        return true
    }

    override fun addLast(x : Double) {
        super.addLast(x)
        if(size > capacity) {
            pollFirst()
        }
    }

    override fun addFirst(x : Double) {
        super.addFirst(x)
        if(size > capacity) {
            pollLast()
        }
    }

    override fun push(x : Double) {
        super.push(x)
        if(size > capacity) {
            pollLast()
        }
    }

}