begin
	! Ett program som inneh�ller en function;
	integer function F(X,Y); integer X; integer Y; F:=X/Y;
	integer I;
	I:=F(23,5)+F(18,3);
end
