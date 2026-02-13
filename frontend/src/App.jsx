import { useState, useEffect } from 'react'
import axios from 'axios'
import './App.css'

function App() {
  const [posts, setPosts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
      // Fetch data from Java API
      axios.get('http://localhost:8080/api/posts')
        .then(response => {
            setPosts(response.data)
            setLoading(false)
        })
        .catch(error => {
            console.error("Error fetching data: ", error)
            setError("Could not connect to backend. Is Java running?")
            setLoading(false)
        })
      }, [])

  if (loading) return <h1>Loading sentiment data...</h1>
  if (error) return <h1 style={{color: 'red'}}>{error}</h1>

  return (
    <>
      <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
            <h1>Reddit Sentiment Dashboard</h1>
            <p>Analyze the mood of r/java, r/programming, and r/technology</p>

            <table border="1" cellPadding="10" style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ background: '#f4f4f4', textAlign: 'left' }}>
                  <th>Sentiment</th>
                  <th>Score</th>
                  <th>Title</th>
                </tr>
              </thead>
              <tbody>
                {posts.map(post => (
                  <tr key={post.id}>
                    {/* Conditional Styling based on Score */}
                    <td style={{
                      fontWeight: 'bold',
                      color: post.sentimentScore > 2 ? 'green' : (post.sentimentScore < 2 ? 'red' : 'gray')
                    }}>
                      {post.sentimentScore > 2 ? 'POSITIVE' : (post.sentimentScore < 2 ? 'NEGATIVE' : 'NEUTRAL')}
                    </td>
                    <td>{post.sentimentScore}</td>
                    <td>
                      <a href={post.url} target="_blank" rel="noopener noreferrer">
                        {post.title}
                      </a>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
    </>
  )
}

export default App
