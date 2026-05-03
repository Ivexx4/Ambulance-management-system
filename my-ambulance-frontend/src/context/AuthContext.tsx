import React, { createContext, useState, useContext, useEffect } from 'react';

interface AuthContextType {
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(() => {
    // Check for token in localStorage on initial load
    return localStorage.getItem('token') ? true : false;
  });

  const login = async (username: string, password: string): Promise<boolean> => {
    try {
      const response = await fetch('/api/session', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `email=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`,
      });

      if (response.ok) {
        const data = await response.json(); // Assuming the API returns some user data or a success message
        localStorage.setItem('token', data.token || 'dummy-token'); // Store token (replace 'dummy-token' with actual token from backend if available)
        setIsAuthenticated(true);
        return true;
      } else {
        // Handle different error statuses
        const errorData = await response.json();
        console.error('Login failed:', errorData.message || response.statusText);
        setIsAuthenticated(false);
        return false;
      }
    } catch (error) {
      console.error('Network error or unexpected issue during login:', error);
      setIsAuthenticated(false);
      return false;
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
