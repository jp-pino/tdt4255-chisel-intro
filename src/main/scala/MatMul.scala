package Ex0

import chisel3._
import chisel3.util.Counter
import chisel3.experimental.MultiIOModule

class MatMul(val rowDimsA: Int, val colDimsA: Int) extends MultiIOModule {

  val io = IO(
    new Bundle {
      val dataInA     = Input(UInt(32.W))
      val dataInB     = Input(UInt(32.W))

      val dataOut     = Output(UInt(32.W))
      val outputValid = Output(Bool())
    }
  )

  val debug = IO(
    new Bundle {
      val myDebugSignal = Output(Bool())
    }
  )


  /**
    * Your code here
    */
  val matrixA     = Module(new Matrix(rowDimsA, colDimsA)).io
  val matrixB     = Module(new Matrix(rowDimsA, colDimsA)).io
  val dotProdCalc = Module(new DotProd(colDimsA)).io
  val controller  = Module(new Controller(rowDimsA, colDimsA)).io

  // Setup
  matrixA.writeEnable := !controller.state
  matrixB.writeEnable := !controller.state

  matrixA.dataIn := io.dataInA
  matrixB.dataIn := io.dataInB

  matrixA.rowIdx := controller.rowIdx
  matrixA.colIdx := controller.colIdx

  matrixB.rowIdx := controller.rowIdx
  matrixB.colIdx := controller.colIdx

  // Execution
  dotProdCalc.dataInA := 0.U
  dotProdCalc.dataInB := 0.U

  printf("Controller state: %d\n", controller.state)

  when(controller.state) {
    val row = RegInit(UInt(8.W), 255.U) // Mat A
    val col = RegInit(UInt(8.W), 0.U) // Mat B
    val ii = RegInit(UInt(8.W), 0.U)  // DotProduct iterator

    matrixA.rowIdx := row
    matrixB.rowIdx := col

    matrixB.colIdx := ii
    matrixA.colIdx := ii

    printf("V? %d | [ %d|%d ] ii: %d (%d, %d): %d <-> %d | %d\n", dotProdCalc.outputValid, rowDimsA.U, colDimsA.U, ii, row, col, matrixA.dataOut, matrixB.dataOut, dotProdCalc.dataOut)
    
    dotProdCalc.dataInA := matrixA.dataOut
    dotProdCalc.dataInB := matrixB.dataOut

    ii := ii + 1.U
    when(ii + 1.U === colDimsA.U ) {
      ii := 0.U
      col := col + 1.U
      when (col + 1.U === rowDimsA.U) {
        col := 0.U
        row := row + 1.U
        when (row + 1.U === rowDimsA.U) {
          row := 0.U
        }
      }
    }
    
  } 

  io.dataOut := dotProdCalc.dataOut
  io.outputValid := dotProdCalc.outputValid && controller.state


  debug.myDebugSignal := false.B
}
