/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;


import entity.Buyer;
import entity.Furniture;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import session.BuyerFacade;
import session.FurnitureFacade;
import session.HistoryFacade;
import session.UserFacade;
import session.UserRolesFacade;

/**
 *
 * @author Comp
 */
@WebServlet(name = "ManagerServlet", urlPatterns = {
    "/addFurniture",
    "/createFurniture",
    "/editFurnitureForm",
    "/editFurniture",
    "/editBuyerForm",
    "/editBuyer",

})
public class ManagerServlet extends HttpServlet {

    @EJB
    private HistoryFacade historyFacade;
    
    @EJB
    private UserFacade userFacade;

    @EJB
    private FurnitureFacade furnitureFacade;

    @EJB
    private BuyerFacade buyerFacade;
    
    @EJB private UserRolesFacade userRolesFacade;
    



    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if(session == null){
            request.setAttribute("info", "У вас нет права для этого ресурса. Войдите в систему");
            request.getRequestDispatcher("/showLoginForm").forward(request, response);
            return;
        }
        User user = (User) session.getAttribute("user");
        if(user == null){
            request.setAttribute("info", "У вас нет права для этого ресурса. Войдите в систему");
            request.getRequestDispatcher("/showLoginForm").forward(request, response);
            return;
        }
        boolean isRole = userRolesFacade.isRole("MANAGER", user);
        if(!isRole){
            request.setAttribute("info", "У вас нет права для этого ресурса. Войдите в систему с соответствующими правами");
            request.getRequestDispatcher("/showLoginForm").forward(request, response);
            return;
        }
        String path = request.getServletPath();
        
        switch (path) {
            case "/addFurniture":
                request.getRequestDispatcher(LoginServlet.pathToJsp.getString("addFurniture")).forward(request, response);
                break;
            case "/createFurniture":
                String name = request.getParameter("name");
                String color = request.getParameter("color");
                String size = request.getParameter("size");
                String quantity = request.getParameter("quantity");
                String price = request.getParameter("price");
               
                if ("".equals(name) || name == null
                        || "".equals(color) || color == null
                        || "".equals(size) || size == null
                        || "".equals(quantity) || quantity == null
                        || "".equals(price) || price == null){
                    request.setAttribute("name", name);
                    request.setAttribute("color", color);
                    request.setAttribute("size", size);
                    request.setAttribute("quantity", quantity);
                    request.setAttribute("price", price);
                    request.setAttribute("info", "Заполните все поля.");
                    request.getRequestDispatcher("/addFurniture").forward(request, response);
                    break;
                }
                else if (Integer.parseInt(price) < 1) {
                    request.setAttribute("info","Цена не может быть меньше 1$!");          
                    request.getRequestDispatcher("/addFurniture").forward(request, response);
                    break; 
                }    
                Furniture furniture = new Furniture(name, color, size, Integer.parseInt(quantity), Integer.parseInt(price));
                furnitureFacade.create(furniture);
                request.setAttribute("info", "Товар\"" +furniture.getName()+ "\" был добавлен");
                request.getRequestDispatcher("/addFurniture").forward(request, response);
                break;
            case "/editFurnitureForm":

                String furnitureId = request.getParameter("furnitureId");
//                furniture = furnitureFacade.find(Long.parseLong(furnitureId));
//                request.setAttribute("furniture", furniture);
                
                request.getRequestDispatcher(LoginServlet.pathToJsp.getString("editFurniture")).forward(request, response);
                
                break;
            case "/editFurniture":
                furnitureId = request.getParameter("furnitureId");
                furniture = furnitureFacade.find(Long.parseLong(furnitureId));
                name = request.getParameter("name");
                color = request.getParameter("color");
                size = request.getParameter("size");
                quantity = request.getParameter("quantity");
                price = request.getParameter("price");
                
                if("".equals(name) || name == null
                        || "".equals(color) || color == null
                        || "".equals(size) || size == null
                        || "".equals(quantity) || quantity == null
                        || "".equals(price) || price == null){
                    request.setAttribute("info","Заполните все поля формы");
                    request.setAttribute("name",name);
                    request.setAttribute("color",color);
                    request.setAttribute("size",size);
                    request.setAttribute("quantity",quantity);
                    request.setAttribute("price",price);
                    request.getRequestDispatcher("/editFurnitureForm").forward(request, response);
                    break; 
                } else if (Integer.parseInt(price) < 1) {
                    request.setAttribute("info","Цена не может быть меньше 1$!");          
                    request.getRequestDispatcher("/editFurnitureForm").forward(request, response);
                    break;    
                }   
                furniture.setName(name);
                furniture.setColor(color);
                furniture.setSize(size);
                furniture.setQuantity(quantity);
                furniture.setPrice(Integer.parseInt(price));
                furnitureFacade.edit(furniture);
                request.setAttribute("furnitureId", furniture.getId());
                request.setAttribute("info","Товар успешно отредактирован: " + furniture.toString() );
                request.getRequestDispatcher(LoginServlet.pathToJsp.getString("index")).forward(request, response);
                break;                
            case "/editBuyerForm":
                String buyerId = request.getParameter("buyerId");
                Buyer buyer = buyerFacade.find(Long.parseLong(buyerId));
                request.setAttribute("buyer", buyer);
                request.getRequestDispatcher(LoginServlet.pathToJsp.getString("editBuyer")).forward(request, response);
                break;
            case "/editBuyer":
                buyerId = request.getParameter("buyerId");
                String firstname = request.getParameter("firstname");
                String lastname = request.getParameter("lastname");
                String phone = request.getParameter("phone");
                String wallet = request.getParameter("wallet");

                if("".equals(firstname) || firstname == null
                        || "".equals(lastname) || lastname == null
                        || "".equals(phone) || phone == null
                        || "".equals(wallet) || wallet == null){
                    request.setAttribute("info", "Поля не должны быть пустыми");
                    request.getRequestDispatcher("/editBuyerForm").forward(request, response);
                    break;
                }
                
                buyer = buyerFacade.find(Long.parseLong(buyerId));
                buyer.setFirstname(firstname);
                buyer.setLastname(lastname);
                buyer.setPhone(phone);
                buyer.setWallet(wallet);
                buyerFacade.edit(buyer);
                request.setAttribute("buyerId", buyer.getId());
                request.setAttribute("info", "Данные покупателя отредактированы");
                request.getRequestDispatcher("/editBuyerForm").forward(request, response);
                break;
            
           
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
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