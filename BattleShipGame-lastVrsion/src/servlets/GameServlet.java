package servlets;

import Engine.BattleShipGame.*;
import com.google.gson.Gson;
import javafx.util.Pair;
import servlets.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "GameServlet", urlPatterns = {"/game"})
public class GameServlet extends HttpServlet {

    private Gson gson = new Gson();
    private RoomsManager roomsManager;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */

    //region servlets requests and handlers

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String requestType = request.getParameter("requestType");

        switch (requestType)
        {
            case Constants.GAMEDETAILS:
                handleGameDetails(request, response);
                break;
            case Constants.DO_MOVE:
                handleDoMove(request, response);
                break;
            case Constants.TURNDONE:
                handleTurnDone(request, response);
                break;
            case Constants.BOARD:
                handleBoard(request, response);
                break;
            case Constants.CHECK_GAME_START:
                handleCheckGameStart(request, response);
                break;
            case Constants.SYSTEM_MESSAGE:
                handleSystemMessage(request, response);
                break;
            case Constants.LEAVE_ROOM:
                handleLeaveRoom(request, response);
                break;
            case Constants.RESET_TO_LAST_MOVE:
                handleResetToLastMove(request, response);
                break;
            case Constants.NUM_OF_ALL_MOVES:
                handlenNmOfAllMoves(request, response);
                break;
            case Constants.UndoMoveWithoutChangeState:
                handleMoveWithoutChangeState(request, response, true);
                break;
            case Constants.RedoMoveWithoutChangeState:
                handleMoveWithoutChangeState(request, response, false);
                break;
        }
    }

    private void handleMoveWithoutChangeState(HttpServletRequest request, HttpServletResponse response, Boolean isUndo) throws IOException {
        response.setContentType("text/plain");
        GameManager gameManager = getGameManager(request);
      //  if (isUndo)
            //gameManager.undoMoveWithoutChangeState(request.getParameter("organizer"));
       // else {
            //gameManager.redoMoveWithoutChangeState(request.getParameter("organizer"));
        }
  //  }

    private void handlenNmOfAllMoves(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GameManager gameManager = getGameManager(request);
       // String responseString = gson.toJson(gameManager.getNmOfAllMoves(request.getParameter("organizer")));
        //response.getWriter().write(responseString);
    }

    private void handleResetToLastMove(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        GameManager gameManager = getGameManager(request);
        String reviewOffset = request.getParameter("reviewOffset");
      //  gameManager.resetToLastMove(request.getParameter("organizer"), Integer.parseInt(reviewOffset));
    }

    private void handleSystemMessage(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        GameManager gameManager = getGameManager(request);
        String responseString = gson.toJson(gameManager.getSystemMessage());
        gameManager.resetGame();
        response.getWriter().write(responseString);
    }

    private void handleLeaveRoom(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        GameManager gameManager = getGameManager(request);
        gameManager.removePlayer(request.getParameter("organizer"));
        Map<String, String> result = new HashMap<>();
        result.put("redirect", "rooms.html");
        String json = gson.toJson(result);
        response.getWriter().write(json);
    }

    private void handleGameDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GameManager gameManager = getGameManager(request);
        GameManager.GameDetails gameDetails = gameManager.getGameDetails(request.getParameter("organizer"));
        String json = gson.toJson(gameDetails);
        response.getWriter().write(json);
    }

    private void handleTurnDone(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GameManager gameManager = getGameManager(request);
      //  gameManager.setNextPlayer();
        Map<String, Boolean> resultParameter = new HashMap<>();
        resultParameter.put("isSuccessful", true);
        String json = gson.toJson(resultParameter);
        response.getWriter().write(json);
    }

    private void handleUndoRedo(HttpServletRequest request, HttpServletResponse response, Boolean isUndo) throws IOException {
        GameManager gameManager = getGameManager(request);
      //  LinkedList<Triplet<Integer, Integer, Board.BoardSign>> moves;
        //if (isUndo)
      //      moves = gameManager.undoMove();
        //else {
       //     moves = gameManager.redoMove();
        }
       // String movesJson = gson.toJson(moves);
      //  PrintWriter out = response.getWriter();
        //out.println(movesJson);
       // out.flush();
  //  }

    private void handleCheckGameStart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GameManager gameManager = getGameManager(request);
        Pair<Boolean, List<Pair<String,  String>>> result = new Pair<>(gameManager.getGameRunning(), gameManager.makePlayerList());
        String responseString = gson.toJson(result);
        response.getWriter().write(responseString);
    }

    private void handleDoMove(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("organizer");
        GameManager gameManager = getGameManager(request);
        Map<String, Object> resultParameter = new HashMap<>();

        if(!gameManager.compareUserToCurrentPlayer(username)){
            resultParameter.put("isSuccessful", false); //case for a different user than current sending request
        }
        else {
            Pair coordinate = gson.fromJson(request.getParameter("selectedCoord"), Pair.class);

            String res = gameManager.makeMoveOptionFX(Integer.parseInt((String) coordinate.getKey()), Integer.parseInt((String) coordinate.getValue())).toString();
            resultParameter.put("isSuccessful", true);
            resultParameter.put("messages", res);
        }

        String json = gson.toJson(resultParameter);
        response.getWriter().write(json);
    }

    private void handleBoard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GameManager gameManager = getGameManager(request);
        int currPlayer;

        if(request.getParameter("organizer").equals(gameManager.Players[0].NAME))
            currPlayer = 0;
        else
            currPlayer = 1;

        Board myBoard = gameManager.Players[currPlayer].GetMyBoard();
        Board trackingBoard = gameManager.Players[currPlayer].GetTrackingBoard();

       SimpleBoards responseBoard = new SimpleBoards(myBoard.getM_squre(), trackingBoard.getM_squre());

        String boardJson = gson.toJson(responseBoard);
        PrintWriter out = response.getWriter();
        out.println(boardJson);
        out.flush();
    }

//endregion


    private GameManager getGameManager(HttpServletRequest request)
    {

        String x = request.getParameter("roomid");

        int roomId = Integer.parseInt(request.getParameter("roomid"));
        if(roomsManager == null){
            roomsManager = ServletUtils.getRoomsManager(getServletContext());
        }
        return roomsManager.getGames().get(roomId);
    }



// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}


