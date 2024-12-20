<template>
    <div class="table-container">
      <table class="user-table">
        <thead>
          <tr>
            <th>用户名</th>
            <th>邮箱</th>
            <th>UID</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in userData" :key="user.uid">
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.uid }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>
  
  <script>
  export default {
    name: 'UserTable',
    props: {
      userData: {
        type: Array,
        required: true
      }
    }
  }
  </script>
  
  <style scoped>
  .table-container {
    margin: 20px auto;
    max-width: 800px;
  }
  
  .user-table {
    width: 100%;
    border-collapse: collapse;
    background-color: white;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    overflow: hidden;
  }
  
  .user-table th,
  .user-table td {
    padding: 12px;
    text-align: left;
    border-bottom: 1px solid #eee;
  }
  
  .user-table th {
    background-color: #a9d4ff;
    color: #3f5bad;
    font-weight: bold;
  }
  
  .user-table tr:hover {
    background-color: #f5f9ff;
  }
  
  .user-table td {
    color: #2c3e50;
  }
  </style>