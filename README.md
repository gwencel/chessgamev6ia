# Jogo de Xadrez em Java

Este é um jogo de xadrez simples implementado em Java, utilizando Swing para a interface gráfica.

## Descrição

Este projeto implementa um jogo de xadrez totalmente funcional com uma interface gráfica. Inclui recursos essenciais como o layout da grade, movimentação das peças e lógica do jogo.

## Funcionalidades

- Jogue xadrez contra um computador ou outro jogador.
- Interface interativa para a grade e as peças.
- Regras básicas do xadrez implementadas.

## Tecnologias Usadas

- Java
- GridLayout (para o layout do tabuleiro)
- Java Swing (para a interface gráfica)

## Como Executar

1. Certifique-se de que o [Java Development Kit (JDK) 17 ou superior](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) esteja instalado em sua máquina.
2. Baixe ou clone o repositório para o seu computador.
3. Navegue até a pasta do projeto (`ChessGameV5`).
4. Compile os arquivos Java:
   ```bash
   javac -d out src/controller/*.java src/model/board/*.java src/model/pieces/*.java src/view/*.java
   ```
5. Execute o jogo:
   ```bash
   java -cp out view.ChessGUI
   ```

## Modificações Recentes

- **Cores adaptáveis**: Sistema de cores dinâmico baseado no tema
- **Atualização automática**: Todos os componentes se atualizam ao trocar tema
- **Compatibilidade**: Funciona com Java 11+
- **Feedback imediato**: Resposta visual para todas as ações
- **Informações claras**: Cronômetro e status sempre visíveis
- **Controles intuitivos**: Botões com ícones e efeitos visuais
- **Botão reiniciar**: Efeitos visuais (sem ícone de texto para compatibilidade de fontes).uais
- **Tamanho otimizado**: Janela ligeiramente maior (950x750)
- **Cores dinâmicas**: Adaptam-se ao tema selecionado
- **Peça selecionada**: Efeito de pulsação (brilho alternado)
- **Movimento bem-sucedido**: Flash verde na casa de destino
- **Indicadores visuais**: 
  - Círculos azuis para movimentos possíveis
  - X vermelho para capturas possíveis
- **Bordas destacadas**: Para peças selecionadas e movimentos
- **Casas do tabuleiro**: Iluminação sutil ao passar o mouse
- **Botões**: Mudança de cor ao passar o mouse

## Funcionalidades da IA

- **Cor das Peças**: Por padrão, a IA joga com as peças pretas
- **Nível de Dificuldade**: Atualmente implementa movimentos aleatórios válidos
- **Validação**: Todos os movimentos da IA são validados pelas mesmas regras do jogador
- **Integração**: A IA se integra perfeitamente ao sistema de turnos existente
- **Interface**: O botão de IA segue o design visual do jogo

### Integração Visual
- O botão de IA foi integrado ao painel lateral existente
- Mantém o mesmo estilo visual dos outros botões
- Efeitos de hover consistentes com o design original
- Cores que se adaptam ao tema claro/escuro

### Funcionalidade
- Bloqueio automático dos movimentos do jogador quando é a vez da IA
- Reset automático do botão ao reiniciar o jogo
- Mensagens informativas sobre o estado da IA
- Integração completa com o sistema de cronometragem

## Notas Técnicas

- A IA utiliza a mesma validação de movimentos que o jogador humano
- Todos os movimentos especiais (roque, en passant, promoção) são suportados
- O sistema de detecção de xeque e xeque-mate funciona normalmente com a IA
- Mantém compatibilidade total com todas as funcionalidades originais
- A estrutura visual original foi preservada integralmente



### ⏱ Cronômetro Funcional
- **Início automático**: Começa a contar no primeiro movimento
- **Pausa automática**: Para quando o jogo termina
- **Reset automático**: Reinicia quando o jogo é reiniciado
- **Exibição clara**: Formato MM:SS no canto superior esquerdo (sem ícone de texto para compatibilidade de fontes).
- **Tempo final**: Mostrado no diálogo de fim de jogo

### 🌓 Sistema de Temas Escuro/Claro
- **Botão de alternância**: Localizado no canto superior direito
- **Tema Escuro**: Cores escuras e elegantes (padrão)
- **Tema Claro**: Cores claras e suaves
- **Transição suave**: Todas as cores se atualizam instantaneamente
- **Texto dinâmico**: O texto do botão muda para "TEMA CLARO" ou "TEMA ESCURO" conforme o tema ativo.

## Autor

Gabriel Wencel

