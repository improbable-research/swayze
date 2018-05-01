package io.improbable.swayze.examples.navierStokes

import io.improbable.swayze.finiteDifference.Field
import io.improbable.keanu.kotlin.ArithmeticDouble
import java.io.File

class OutputWriter (val variableName: String, val params: FieldParams) {

    val folderName = "__output/" + variableName
    val file = File(folderName + "/" + variableName + ".csv")


    private fun header (state: Field<ArithmeticDouble>) {
        file.writeText("variable: " + variableName + "\n")
        file.appendText("rank: " + 0 + "\n")
        file.appendText("dimensionality: " + 0 + "\n")
        file.appendText("dx: " + params.DX + "\n")
        file.appendText("dy: " + params.DY + "\n")
        file.appendText("dt: " + params.DT + "\n")
        file.appendText("T: " + params.TLENGTH + "\n")
        file.appendText("iSize: " + state.iSize() + "\n")
        file.appendText("jSize: " + state.jSize() + "\n\n")
    }


    private fun writeCSV(time: Double, state: Field<ArithmeticDouble>) {
        if (time == 0.0) {header(state)}
        file.appendText("t: " + time.toString() + "\n")
        file.appendText("u: ")
        for (i in 0 until state.iSize()) {
            for (j in 0 until state.jSize()) {
                file.appendText("${state[i,j].value}, ")
            }
        }
        file.appendText("\n\n")
    }
}