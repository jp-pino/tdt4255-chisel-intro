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


  val (counter, wrap) = Counter(true.B, rowDimsA * colDimsA)
  val state = RegInit(false.B)

  when(wrap) {
    state := true.B
    matrixA.writeEnable := false.B
    matrixB.writeEnable := false.B
  } 
  
  // Setup
  matrixA.rowIdx := counter / colDimsA.U
  matrixA.colIdx := counter % colDimsA.U
  matrixA.dataIn := io.dataInA
  matrixA.writeEnable := true.B

  matrixB.rowIdx := counter / colDimsA.U
  matrixB.colIdx := counter % colDimsA.U
  matrixB.dataIn := io.dataInB
  matrixB.writeEnable := true.B

  // Execution
  when(state) {
    
    // Setup
    dotProdCalc.dataInA := 0.U
    dotProdCalc.dataInB := 0.U
  
    
  }

  dotProdCalc.dataInA := 0.U
  dotProdCalc.dataInB := 0.U

  io.dataOut := 0.U
  io.outputValid := false.B


  debug.myDebugSignal := false.B
}
