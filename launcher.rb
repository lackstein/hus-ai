Shoes.app {
  stack(margin: 8) {
    title "Enter genome"
    flow {
      @genome1 = edit_line
      button("Paste").click {
        @genome1.text = clipboard()
      }
      button("Default").click {
        @genome1.text = "30 30 15 0 0 -10"
      }
    }
    flow {
      @genome2 = edit_line
      button("Paste").click {
        @genome2.text = clipboard()
      }
    }
    @genome1.text = "30 30 15 0 0 -10"

    flow {
      button("Play!").click {
        Thread.new { `java -cp bin boardgame.Server` }
        sleep 2
        Thread.new { `ALPHA_GENOME="#{@genome1.text}" java -cp bin boardgame.Client alpha_player.AlphaPlayer` }
        Thread.new { `BETA_GENOME="#{@genome2.text}" java -cp bin boardgame.Client beta_player.BetaPlayer` }
      }

      button("Swap").click {
        old = @genome1.text
        @genome1.text = @genome2.text
        @genome2.text = old
      }
    }
  }
}
