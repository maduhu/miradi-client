class AttemptLoginController < ApplicationController
  def index
  	if request.post?
      email = params[:user][:email]
      password = params[:user][:password]
      
  	  u = User.authenticate(email, password)
      if u
        session[:user] = u 
        flash[:message]  = "Login successful"
      else
        flash[:warning] = "Login unsuccessful"
      end
    end
    
    if session[:user]
      redirect_to "/download"
    else
      redirect_to "/login"
    end
  end
end
