
import { useState } from 'react';
import axios from 'axios';
import './AuthForm.css';

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ||
  'http://localhost:8080';
  
const API_URL = `${API_BASE_URL}/api`;


function AuthForm({ onLogin }) {
  const [isRegister, setIsRegister] = useState(false);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(() => {
    const params = new URLSearchParams(window.location.search);
    return params.get('oauthError') || '';
  });
  const [loading, setLoading] = useState(false);

 const handleGoogleAuth = () => {
  window.location.href =
    `${API_BASE_URL}/oauth2/authorization/google`;
};

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (isRegister) {
        await axios.post(`${API_URL}/register`, {
          name,
          email,
          password,
        });

        setIsRegister(false);
        setPassword('');
        setError('Account created! Please log in.');
        return;
      }

      const response = await axios.post(`${API_URL}/login`, {
        email,
        password,
      });

      onLogin(response.data.token);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          'Request failed. Please check your details and try again.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="auth-container">
      <h1>PulseWatch</h1>
      <p>Website &amp; API Health Monitoring</p>

      <h2>{isRegister ? 'Create account' : 'Welcome back'}</h2>

      <form onSubmit={handleSubmit}>
        {isRegister && (
          <label>
            Name
            <input
              value={name}
              onChange={(event) => setName(event.target.value)}
              required
            />
          </label>
        )}

        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
          />
        </label>

        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            minLength={8}
            required
          />
        </label>

        {error && <p role="alert">{error}</p>}

        <button type="submit" disabled={loading}>
          {loading
            ? 'Please wait...'
            : isRegister
              ? 'Register'
              : 'Login'}
        </button>
      </form>

      <div className="google-auth">
        <p>Or</p>
        <button type="button" onClick={handleGoogleAuth}>
          {isRegister ? 'Sign up with Google' : 'Continue with Google'}
        </button>
      </div>

      <p>
        {isRegister ? 'Already have an account?' : 'New to PulseWatch?'}{' '}
        <button
          type="button"
          onClick={() => {
            setIsRegister(!isRegister);
            setError('');
          }}
        >
          {isRegister ? 'Login' : 'Register'}
        </button>
      </p>
    </main>
  );
}

export default AuthForm;
