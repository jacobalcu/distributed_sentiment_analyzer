import { useState, useEffect } from 'react'
import axios from 'axios'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import './App.css'

function App() {
  const [posts, setPosts] = useState([])
  const [chartData, setChartData] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
      // Fetch data from Java API
      axios.get('http://localhost:8080/api/posts')
        .then(response => {
            const data = response.data
            setPosts(response.data)

            // Aggregation logic
            // Count freq of each score
            const counts = {0:0, 1:0, 2:0, 3:0, 4:0};
            data.forEach(post => {
                if (counts[post.sentimentScore] !== undefined) {
                    counts[post.sentimentScore]++;
                 }
             });

            // Format for Recharts
            const formattedData = [
                        { name: 'Very Negative', count: counts[0], fill: '#d32f2f' },
                        { name: 'Negative', count: counts[1], fill: '#f44336' },
                        { name: 'Neutral', count: counts[2], fill: '#9e9e9e' },
                        { name: 'Positive', count: counts[3], fill: '#4caf50' },
                        { name: 'Very Positive', count: counts[4], fill: '#388e3c' },
                    ];
            setChartData(formattedData);
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
      <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
            <h1>Reddit Sentiment Dashboard</h1>

            {/* --- THE CHART SECTION --- */}
            <div style={{ height: '300px', marginBottom: '40px' }}>
              <h3>Sentiment Distribution</h3>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis allowDecimals={false} />
                  <Tooltip />
                  <Bar dataKey="count" />
                </BarChart>
              </ResponsiveContainer>
            </div>

            {/* --- THE TABLE SECTION --- */}
            <h3>Recent Posts</h3>
            <table style={{ width: '100%', borderCollapse: 'collapse', border: '1px solid #ddd' }}>
              <thead style={{ background: '#f4f4f4' }}>
                  <tr>
                      <th style={{ padding: '10px' }}>Score</th>
                      <th style={{ padding: '10px' }}>Title</th>
                  </tr>
              </thead>
              <tbody>
                {posts.map(post => (
                  <tr key={post.id} style={{ borderBottom: '1px solid #ddd' }}>
                    <td style={{
                      textAlign: 'center',
                      fontWeight: 'bold',
                      color: post.sentimentScore > 2 ? 'green' : (post.sentimentScore < 2 ? 'red' : 'gray')
                    }}>
                      {post.sentimentScore}
                    </td>
                    <td style={{ padding: '10px' }}>
                        <a href={post.url} target="_blank" rel="noopener noreferrer" style={{ textDecoration: 'none', color: '#333' }}>
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
