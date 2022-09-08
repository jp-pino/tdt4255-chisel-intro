package Ex0

import chisel3._
import chisel3.util.Counter
import chisel3.experimental.MultiIOModule

class Controller(val rowDimsA: Int, val colDimsA: Int) extends MultiIOModule {
  val io = IO(
    new Bundle {
      val rowIdx = Output(UInt(32.W))
      val colIdx = Output(UInt(32.W))
      val state = Output(Bool())
    }
  )

  val (counter, wrap) = Counter(true.B, rowDimsA * colDimsA)
  val previousState = RegInit(false.B)
  io.state := previousState

  // printf("WRAP: %d\nPREV STATE: %d\nOUT STATE: %d\n", wrap, previousState, io.state)

  when(wrap) {
    when(!previousState) {
      previousState := true.B
    }
  } 
  io.rowIdx := counter / colDimsA.U
  io.colIdx := counter % colDimsA.U
}